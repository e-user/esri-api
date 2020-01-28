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

(ns esri_api.data
  "Data import and transformation"
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [java-time :refer [local-date]])
  (:import [java.util Date]))

(defn import-csv
  "Import CSV resource"
  ([res]
   (with-open [r (io/reader (io/resource res))]
     (doall (csv/read-csv r))))
  ([] (import-csv "Adressen__Berlin.csv")))

(defn parse-date
  "Parse date strings of the format `2008-01-28T00:00:00`"
  [s]
  (local-date (java-time.format/formatter :iso-local-date-time) s))

(defn csv-data->maps
  "Transform raw CSV to hash maps

  Also transform select data fields."
  [csv-data]
  (map (fn [keys vals]
         (-> (zipmap keys vals)
           (update :STR_DATUM parse-date)))
    (->> (first csv-data)
      (map keyword)
      repeat)
    (rest csv-data)))

(def csv
  "Future of imported and transformed CSV data set"
  (future (csv-data->maps (import-csv))))
