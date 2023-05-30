(ns net.lewisship.star-wars
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [nextjournal.clerk :as clerk]))

(def people-raw (json/read (io/reader (io/resource "data/star-wars/people-1.json"))
                           :key-fn keyword))

(defn- set-from-comma-separated
  [s]
  (set (map keyword
            (string/split s #"\s*,\s*"))))

(defn simplify-person
  "Filter and rename keys, convert values to be more natural in Clojure."
  [person]
  {:birth-year (:birth_year person)
   :height     (Long/parseLong (:height person))
   :gender     (keyword (:gender person))
   :name       (:name person)
   :films      (:films person)
   :eye-color  (keyword (:eye_color person))
   :mass       (Long/parseLong (:mass person))
   :hair-color (set-from-comma-separated (:hair_color person))
   :skin-color (set-from-comma-separated (:skin_color person))})


(for [person (:results people-raw)]
  (simplify-person person))

(let [raw-persons (:results people-raw)
      persons     (map simplify-person raw-persons)
      just-mass   (map :mass persons)
      total-mass  (apply + just-mass)]
  total-mass)

(defn total-mass
  []
  (loop [result 0
         coll   (map simplify-person (:results people-raw))]
    (if (some? coll)
      (recur (+ result (:mass (first coll)))
             (next coll))
      result)))

(total-mass)

(def people (->> people-raw
                 :results
                 (map simplify-person)))

(filter #(= :blue (:eye-color %)) people)

(->> people
     (map (fn [person]
            (let [name (:name person)]
              (map (fn [film]
                     {:name name
                      :film film})
                   (:films person)))))
     (reduce into [])
     (reduce (fn [film->names film+name]
               (update film->names (:film film+name)
                       conj (:name film+name)))
             {}))

(let [person->file+names (fn [person]
                           (let [name (:name person)]
                             (map (fn [film]
                                    {:name name
                                     :film film})
                                  (:films person))))
      combine-file+names (fn [film->names film+name]
                           (update film->names (:film film+name)
                                   conj (:name film+name)))]
  (->> people
       (map person->file+names)
       (reduce into [])
       (reduce combine-file+names {})))
