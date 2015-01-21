(ns rente.run
  (:gen-class)
  (:refer-clojure :exclude [refresh])
  (:require 
   			[clojure.tools.namespace.repl :refer (refresh refresh-all)]
  			[clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [rente.config :as config]
            [rente.system :refer [system]]))

(defn -main [& args]
  (let [config (config/get-config)]
    (component/start (system config))
    (log/info "rente started")))
