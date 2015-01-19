(ns rente.client.app
    (:require-macros [cljs.core.async.macros :refer [go-loop]])
    (:require [reagent.core :as reagent]
              [rente.client.views :as views]
              [rente.client.ws :as ws]))

(defonce state (reagent/atom {:title "RENTE"
                              :messages []

                              :re-render-flip false}))


(defmulti handle-event (fn [data [ev-id ev-data]] ev-id))

(defmethod handle-event :default
  [data [_ msg]]
  (swap! data update-in [:messages] #(conj % msg)))

(defn event-loop [data]
  (go-loop []
    (let [msg (<! ws/ch-chsk)]
      (when msg
        (let [[op arg] (:event msg)]
          (case op
            :chsk/recv (handle-event data op)
            nil))
        (recur)))))

(defn app [data]
  (event-loop data)
  (:re-render-flip @data)
  [views/main data])


(defonce seconds-elapsed (reagent/atom 0))
(defn timer-component [data]
    (fn []
      (js/setTimeout #(swap! data inc) 1000)
      [:div
       "Seconds Elapsed since...: " @data]))
(defn timer-component2 []
  (let [seconds-elapsed (reagent/atom 0)]     ;; setup, and local state
    (fn []        ;; inner, render function is returned
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div "Seconds Elapsed: " @seconds-elapsed])))

(defn some-component []
  [:div
   [:h3 "I am some component!"]
   [:p.someclass 
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]])

(defn counting-component []
  (let [click-count (reagent/atom 0)]
    (fn []  
    [:div
    "The atom " [:code "click-count"] " has value: "
    @click-count ". "
    [:input {:type "button" :value "Click me!"
            :on-click #(swap! click-count inc)}]])))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn shared-state []
  (let [val (reagent/atom "foo")]
    (fn []
      [:div
       [:p "The value is now: " @val]
       [:p "Change it here: " [atom-input val]]])))


(def timer (reagent/atom (js/Date.)))
(def time-color (reagent/atom "#f34"))
(defn update-time [time]
  ;; Update the time every 1/10 second to be accurate...
  (js/setTimeout #(reset! time (js/Date.)) 100))
(defn greeting [message]
  [:h3 message])
(defn clock []
  (update-time timer)
  (let [time-str (-> @timer .toTimeString (clojure.string/split " ") first)]
    [:div.example-clock
     {:style {:color @time-color}}
     time-str]))
(defn color-input []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @time-color
            :on-change #(reset! time-color (-> % .-target .-value))}]])
(defn clock-example []
  [:div
   [greeting "A simple clock"]
   [clock]
   [color-input]])

(defn ^:export main []
  (when-let [root6 (.getElementById js/document "app6")]
    (reagent/render-component [clock-example] root6))
  (when-let [root5 (.getElementById js/document "app5")]
    (reagent/render-component [shared-state] root5))
  (when-let [root4 (.getElementById js/document "app4")]
    (reagent/render-component [counting-component] root4))
  (when-let [root3 (.getElementById js/document "app3")]
    (reagent/render-component [timer-component2] root3))
  (when-let [root2 (.getElementById js/document "app2")]
    (reagent/render-component [some-component state] root2))
  (when-let [root (.getElementById js/document "app")]
    (reagent/render-component [app state] root)))