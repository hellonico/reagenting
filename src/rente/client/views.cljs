(ns rente.client.views
  (:require [rente.client.ws :as socket]))

(defn main [data]
  [:div
   [:h1 (:title @data)]
   [:blockquote.p "The base sample, using websockets to communicate with the clojure code on the server."]
   [:span "Hello world! This is reagent!"]
   [:br]
   [:span "And sente seems to work too.."]
   [:br]
   [:span "And figwheel.. w00t!"]
   [:br]
   [:button.btn.btn-info {:on-click socket/test-socket-broadcast} "Send Message"]
   [:br]
   [:button.btn.btn-info {:on-click socket/test-socket-callback} "Send Message Callback"]
   [:br]
   [:button.btn.btn-info {:on-click socket/test-socket-event} "Send Message Event"]
   [:table.table.table-striped.table-hover
   [:th "List of messages"]
   [:tbody
   (for [msg (:messages @data)]
          [:tr [:td (str msg)]]
          )]]])