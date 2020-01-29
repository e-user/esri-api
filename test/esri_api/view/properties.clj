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

(ns esri_api.view.properties
  (:require [esri_api.view :as view]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.string :as string]
            [clojure.set :as set]
            [java-time :refer [with-zone local-date-time instant]]
            [java-time.format])
  (:import [java.time LocalDateTime ZoneId]))

(def date-formatter
  (with-zone (java-time.format/formatter "yyyy-MM-dd'T'HH:mm:ss") "UTC"))

(def iso-date
  (gen/fmap #(LocalDateTime/ofInstant (instant %) (ZoneId/of "UTC"))
    (gen/large-integer* {:min 1000})))

(def csv-data
  (gen/let [n gen/nat
            ks (gen/vector gen/keyword n)]
    (gen/vector
      (gen/fmap (fn [[d v]] (zipmap (conj ks :STR_DATUM) (conj v d)))
        (gen/tuple iso-date (gen/vector (gen/not-empty gen/string-alphanumeric) n))))))

(defspec counting-by-years-maps-years-to-buildings
  (prop/for-all [v csv-data]
    (let [csv (view/by-year v)]
      (for [k (keys csv)]
        (Long/parseLong k))
      (for [vs (vals csv)]
        (or (empty? vs)
          (= (count (dedupe (map keys vs))) 1))))))
