;;;; This file is part of esri-api.
;;;;
;;;; Copyright 2020 Alexander Dorn
;;;;
;;;; Licensed under the Apache License, Version 2.0 (the "License");
;;;; you may not use this file except in compliance with the License.
;;;; You may obtain a copy of the License at
;;;;
;;;;     http://www.apache.org/licenses/LICENSE-2.0
;;;;
;;;; Unless required by applicable law or agreed to in writing, software
;;;; distributed under the License is distributed on an "AS IS" BASIS,
;;;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;;; See the License for the specific language governing permissions and
;;;; limitations under the License.

(ns esri_api.server
  "Web server and routing"
  (:require [esri_api.view :as view]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.coercion :as coercion]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [clojure.spec.alpha :as s]))

(defn buildings-handler
  "Handler for counting buildings by zip code"
  [{:keys [query-params]}]
  {:status 200 :body (if-let [zip (query-params "zip")]
                       (str (or (@view/count-by-zip zip) 0))
                       @view/count-by-zip)})

(defn buildings-year-handler
  "Handler for the distribution of buildings by year"
  [{:keys [query-params]}]
  {:status 200 :body (if-let [zip (query-params "zip")]
                       (view/count-by-year zip)
                       (view/count-by-year))})

(s/def ::zip string?)
(s/def ::query-params (s/keys :opt-un [::zip]))

(defn app-handler []
  (ring/ring-handler
    (ring/router
      [["/api/v1"
        ["/buildings" {:get {:handler buildings-handler
                             :summary "Number of buildings per zip code (optional filter)"
                             :coercion reitit.coercion.spec/coercion
                             :parameters {:query ::query-params}}}]
        ["/buildings-by-year" {:get {:handler buildings-year-handler
                                     :summary "Distribution of buildings by year added, filtered by zip code (optional filter)"
                                     :coercion reitit.coercion.spec/coercion
                                     :parameters {:query ::query-params}}}]]
       ["" {:no-doc true}
        ["/swagger.json" {:get (swagger/create-swagger-handler)
                          :swagger {:info {:title "Esri ArcGIS API"}}}]]]
      {:data {:muuntaja m/instance
              :middleware [muuntaja/format-middleware
                           parameters/parameters-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"})
      (ring/create-default-handler))))

(defrecord WebServer [port server]
  component/Lifecycle

  (start [this]
    (assoc this :server (jetty/run-jetty (app-handler) {:port port :join? false})))

  (stop [this]
    (.stop server)
    (assoc this :server nil)))

(defn web-server [port]
  (map->WebServer {:port port}))

(defn system [{:keys [port] :as options}]
  (component/system-map
    :web-server (web-server port)))
