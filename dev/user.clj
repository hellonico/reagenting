(ns user
  (:require [clojure-watch.core :refer [start-watch]])
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [com.stuartsierra.component :as component]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [rente.config :as config]
   [rente.system :as system]))

(def system nil)

(defn init []
  (alter-var-root #'system (fn [s] (system/system (config/get-config)))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn run []
  (init)
  (start))

; reloads too much, ws screwing up
(defn reset []
  (stop)
  (refresh :after 'user/run))

(defn reload-app
  []
  (component/stop (get system :http-server))
  (refresh :after 'user/run))

(defn watch[]  
  (start-watch [{:path "src"
               :event-types [:create :modify :delete]
               :bootstrap (fn [path] (println "Starting to watch " path))
               :callback (fn[event filename] (doall (reload-app)))
               :options {:recursive true}}]))