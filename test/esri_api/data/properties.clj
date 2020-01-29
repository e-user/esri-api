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

(ns esri_api.data.properties
  (:require [esri_api.data :as data]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.string :as string]
            [clojure.set :as set]
            [java-time :refer [with-zone instant]]
            [java-time.format]))

(def date-formatter
  (with-zone (java-time.format/formatter "yyyy-MM-dd'T'HH:mm:ss") "UTC"))

(def csv-data
  (gen/bind (gen/fmap inc gen/nat)
    #(gen/vector (gen/not-empty (gen/vector (gen/not-empty gen/string-alphanumeric) %)))))

(def iso-date
  (gen/fmap #(java-time.format/format date-formatter (instant %))
    (gen/large-integer* {:min 1000})))

(defspec csv-data-is-maps-of-identical-keys
  (prop/for-all [v csv-data]
    (let [csv (data/csv-data->maps v)]
      (or (empty? csv)
        (= (count (dedupe (map keys csv))) 1)))))

(defspec dates-are-parsed-as-date-time
  (prop/for-all [v iso-date]
    (= (java-time.format/format date-formatter (data/parse-date v)) v)))
