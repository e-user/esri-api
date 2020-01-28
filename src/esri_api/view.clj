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

(ns esri_api.view
  "Data views"
  (:require [esri_api.data :as data]))

(def by-zip
  "Future of zipcode->[building]"
  (future (group-by :PLZ @data/csv)))

(defn by-year
  "Group buildings by the year they were built"
  [coll]
  (group-by (comp str java-time/year :STR_DATUM) coll))

(defn count-vals
  "Tranform k->[v] to k->(count v)"
  [coll]
  (into {} (map (fn [[k v]] [k (count v)])) coll))

(def count-by-zip
  "Future of zipcode->(count buildings)"
  (future (count-vals @by-zip)))

(defn count-by-year
  "Distribution of buildings by year

  Optionally filtered by `zip` code."
  ([]
   (count-vals (by-year @data/csv)))
  ([zip]
   (count-vals (by-year (@by-zip zip)))))
