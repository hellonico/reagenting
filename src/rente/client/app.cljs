(ns rente.client.app
    (:require-macros [cljs.core.async.macros :refer [go-loop]])
    (:require 
              [secretary.core :as secretary :refer-macros [defroute]]
              [ajax.core :refer [GET POST]]
              [reagent.core :as reagent]
              [rente.client.views :as views]
              [rente.client.ws :as ws]
              ))

;;;
;; BASIC EXAMPLE
;;;
(defonce state (reagent/atom 
  {:title "Websocket Example"
   :messages ["test 1"]
   :re-render-flip false}))

(defmulti handle-event (fn [data [ev-id ev-data]] ev-id))

(defmethod handle-event :default
  [data [_ msg]]
  (.log js/console (str ">" data))
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

;;;
;; TIME EXAMPLE
;;;
(defn timer-component2 []
  (let [seconds-elapsed (reagent/atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div
      [:h1 "Dynamic timer example"]
      [:div "Seconds Elapsed: " @seconds-elapsed]])))

;;;
;; STATIC EXAMPLE
;;;
(defn some-component []
  [:div
  [:h1 "Static example"]
   [:div.col-sm-3]
   [:div.col-sm-9
   [:h3 "I am some static component!"]
   [:p.someclass 
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]]])

;;;
;; COUNT EXAMPLE
;;;

(defn counting-component []
  (let [click-count (reagent/atom 0)]
    (fn []  
    [:div
    [:h1 "Counting example"]
    "The atom " [:code "click-count"] " has value: "
    @click-count ". "
    [:button.btn.btn-danger {:type "button"
            :on-click #(swap! click-count inc)} "Click me!"]])))

;;;
;; SHARED STATE EXAMPLE
;;;
(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])
(defn shared-state []
  (let [val (reagent/atom "foo")]
    (fn []
      [:div
       [:h1 "Shared State Example"]
       [:p "The value is now: " @val]
       [:p "Change it here: " [atom-input val]]])))

;;;
;; CLOCK EXAMPLE
;;;
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
   [:h1 "Clock Example"]
   [greeting "A simple clock"]
   [clock]
   [color-input]])

;;;
;; CHART EXAMPLE
;;;
(defonce chart-data (reagent/atom [
     {:country "China" :visits 2808} 
     {:country "USA" :visits 2300}
     {:country "France" :visits 2500}
     {:country "Japan" :visits 3000}]))

(defn chart-rows []
  [:div#chartdata 
        [:table.table.table-striped.table-hover
        ; this <tbody> is added externally if not present
        ; important to keep it here
         [:thead
          [:tr [:th "Country"]]]
         [:tbody
          
         (for [d @chart-data]
          [:tr [:td {:on-click #(js/alert (str (d :country) "->" (d :visits)))} (str (d :country))]]
          )
        ]]])

(defn amcharts-example[]
  (fn[]
    (.makeChart js/AmCharts "chartdiv" (clj->js {
     :type "serial"
     :theme "none"
     :dataProvider @chart-data
     :valueAxes [{
        :gridColor "#FFFFFF"
        :gridAlpha 0.3
        :dashLength 0
     }]
     :gridAboveGraphs true
     :startDuration 1
     :graphs [{
        :balloonText "[[category]]: <b>[[value]]</b>"
        :fillAlphas 0.8
        :lineAlpha 0.2
        :type :column
        :valueField :visits
     }]
     :chartCursor {
        :categoryBalloonEnabled false
        :cursorAlpha 0
        :zoomable true
     }
     :categoryField :country
     :categoryAxis {
        :gridPosition "start"
        :gridAlpha 0
        :tickPosition "start"
        :tickLength 20
     }
     :exportConfig {
     :menuTop 0
     :menuItems [{
      :icon "/lib/3/images/export.png"
      :format 'png'   
      }] 
    }}))
      [:div
        [:h1 "Charts Example"]
        [chart-rows]
        [:div#chartdiv "hello amcharts"]
        [:button.btn.btn-warning 
        {:type "button"
         :on-click #(swap! chart-data conj {:country (str "Country_x") :visits (+ 2000 (rand 1000))})}
         "Add data"
         ]]
    ))

;;;
;; AJAX EXAMPLE
;;;
(defonce weather-data 
  (reagent/atom {}))
(defn ajax-handler [response]
  (let [city (first (response "list"))]
  (reset! weather-data {:city (city "name") :temperature (get-in city ["main" "temp"])}))
  (.log js/console (str response)))
(def tokyo-weather 
  "http://api.openweathermap.org/data/2.5/find?q=Tokyo&units=metric")
(defn ajax-example[]
  [:div
  [:h1 "Weather Example"]
  (if (@weather-data :city)
  [:div.col-sm-3
  [:table.table.table-striped.table-hover
  [:tr [:td (@weather-data :city)]]
  [:tr [:td (@weather-data :temperature)]]]])
  [:br]
  [:button.btn.btn-danger {:type "button"
      :on-click 
      #(GET tokyo-weather {:handler ajax-handler})} "Ajax me!"]])

;;;
;; ROUTING EXAMPLE
;;;
(defonce routing-data (reagent/atom {}))
(defroute routing "/:id" [id]
  (reset! routing-data {:id id}))
(defn routing-example[]
  [:div
  [:h1 "Routing Example"]
  [:span (if-let [msg (@routing-data :id)]
          (str "Current route is " msg)
          (str "No route clicked yet"))
       ]
  [:button.btn.btn-danger {:type "button"
      :on-click #(secretary/dispatch! "/two")} "Route 1"]
  [:button.btn.btn-info {:type "button"
      :on-click #(secretary/dispatch! "/one")} "Route 2"]])

;;;
;; MAIN LOADING
;;;
(defn ^:export main []
  (when-let [root0 (.getElementById js/document "app0")]
    (reagent/render-component [routing-example] root0))
  (when-let [root8 (.getElementById js/document "app8")]
    (reagent/render-component [clock-example] root8))
  (when-let [root7 (.getElementById js/document "app7")]
    (reagent/render-component [ajax-example] root7))
  (when-let [root6 (.getElementById js/document "app6")]
    (reagent/render-component [amcharts-example] root6))
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
