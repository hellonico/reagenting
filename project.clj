(defproject rente "0.1.0"
  :description "Proof of Concept for running reagent+sente on Heroku"
  :url "http://enterlab.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [
                 ; core
                 [org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/clojurescript "0.0-2665"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.stuartsierra/component "0.2.2"]
                 [environ "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [org.clojure/tools.logging "0.3.1"]

                 [ring/ring-core "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [compojure "1.3.1"]
                 [http-kit "2.1.19"]
                 [com.taoensso/sente "1.3.0" :exclusions [org.clojure/tools.reader]]
                 [secretary "1.2.1"]

                 [com.cognitect/transit-clj "0.8.259" :exclusions [commons-codec]]
                 [com.cognitect/transit-cljs "0.8.199"]

                 [reagent "0.5.0-alpha"]
                 
                 ; optional
                 [org.webjars/bootstrap "3.3.1"]
                 [org.webjars/amcharts "3.10.0"]
                 [org.webjars/d3js "3.5.3"]

                 [cljs-ajax "0.3.9"]
                 [org.webjars/jquery-ui "1.11.2"]
                 [org.webjars/jquery "2.1.3"]
                 ]

  :plugins [
    [lein-cljsbuild "1.0.4-SNAPSHOT"]
    [lein-garden "0.2.5"]
  ]
  :source-paths ["src"]
  :resource-paths ["resources" "resources-index/prod"]
  :target-path "target/%s"

  :main ^:skip-aot rente.run

  :less {:source-paths ["src/less"]
        :target-path "resources/public/css"}

   :garden {:builds [{;; Optional name of the build:
                     :id "screen"
                     ;; Source paths where the stylesheet source code is
                     :source-paths ["src-css/garden"]
                     ;; The var containing your stylesheet:
                     :stylesheet garden.myfirst/screen
                     ;; Compiler flags passed to `garden.core/css`:
                     :compiler {;; Where to save the file:
                                :output-to "resources/public/css/garden.css"
                                ;; Compress the output?
                                :pretty-print? true}}]}

  :cljsbuild
  {:builds
   {:client {:source-paths ["src/rente/client"]
             :compiler
             {:output-to "resources/public/js/app.js"
              :output-dir "dev-resources/public/js/out"}}}}

    :profiles {:dev-config {}


               :dev [:dev-config
                   {:dependencies [
                                   [org.clojure/tools.nrepl "0.2.7"]
                                   [org.clojure/tools.namespace "0.2.8"]
                                   [figwheel "0.2.2-SNAPSHOT"]
                                   [org.webjars/react "0.12.1"]]

                    :plugins [
                    [lein-figwheel "0.2.2-SNAPSHOT" :exclusions [org.clojure/tools.reader org.clojure/clojurescript clj-stacktrace]]
                    [lein-environ "1.0.0"]]

                    :source-paths ["dev"]
                    ;:repl-options 
                    ;    {:init (load-file "dev/user.clj")}
                    :resource-paths ^:replace
                      ["resources" "dev-resources" "resources-index/dev"]

                    :cljsbuild
                    {:builds
                     {:client {:source-paths ["dev"]
                               :compiler
                               {:optimizations :none
                                :source-map true}}}}

                    :figwheel {:http-server-root "public"
                               :port 3449
                               :css-dirs ["resources/public/css"]}}]

             :prod {:cljsbuild
                    {:builds
                     {:client {:compiler
                               {:optimizations :advanced
                                :preamble ["reagent/react.min.js"]
                                :pretty-print false}}}}}}

  :aliases {"package"
            ["with-profile" "prod" "do"
             "clean" ["cljsbuild" "once"]]})
