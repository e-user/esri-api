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

(ns esri_api.core
  "Core namespace"
  (:gen-class)
  (:require [esri_api.server :as server]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [com.stuartsierra.component :as component]))

(defn usage [options-summary]
  (format
    "Esri ArcGIS open data API

USAGE:
    esri-api [options]

OPTIONS:
%s"
    options-summary))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond (options :help)
          (do (println (usage summary))
              (System/exit 0))

          errors
          (do (println (string/join \newline errors))
              (System/exit 1))

          (not= 0 (count arguments))
          (do (println (usage options))
              (System/exit 1))

          :default (component/start
                     (server/system {:port (options :port)})))))
