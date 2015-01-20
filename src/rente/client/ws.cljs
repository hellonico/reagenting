(ns rente.client.ws
  (:require [taoensso.sente :as sente]
            [taoensso.sente.packers.transit :as sente-transit]))

;;;
;; HANDLERS: MSG COMING FROM SERVER
;;;
(defmulti event-msg-handler :id) ; Dispatch on event-id
;; Wrap for logging, catching, etc.:

(defmethod event-msg-handler :default ; Fallback
    [{:as ev-msg :keys [event]}]
    (js/console.log "Unhandled event: %s" (pr-str event)))

(defmethod event-msg-handler :rente/testevent
  [{:as ev-msg :keys [?data]}]
    (js/console.log "Testevent received from server"))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (if (= ?data {:first-open? true})
    (js/console.log "Channel socket successfully established!")
    (js/console.log "Channel socket state change: %s" (pr-str ?data))))

(defmethod event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (js/console.log "Push event from server: %s" (pr-str ?data)))

;;;
;; CORE INIT METHODS
;;; 
; the main message handler that does the dispatching on the client side
(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  ;(js/console.log "Event: %s" (pr-str event))
  (event-msg-handler ev-msg))

(let [packer (sente-transit/get-flexi-packer :edn)
      {:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" {:type :auto :packer packer})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

; start channel
(sente/start-chsk-router! ch-chsk event-msg-handler*)

;;;
;; CALLS: MSG TO BE SENT TO SERVERS
;;; 
(defn test-socket-event2 []
  (chsk-send! [:rente/testevent2 {:message "It is time for coffee!"}]))

(defn test-socket-broadcast []
  (chsk-send! [:rente/testevent3 {:message "It is time for coffee!"}]))

(defn test-socket-callback []
  (chsk-send! [:rente/testevent {:message "Hello socket Callback!"}] 2000 #(js/console.log "Callback received: " (pr-str %))))

(defn test-socket-event []
  (chsk-send! [:rente/testevent {:message "Hello socket Event!"}]))
