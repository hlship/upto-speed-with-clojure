(ns net.lewisship.star-wars
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [nextjournal.clerk :as clerk]))

(def people-docs (apply conj []
                        (for [i (range 1 10)
                              :let [path   (str "data/star-wars/people-" i ".json")
                                    url    (io/resource path)
                                    reader (io/reader url)]]
                          (json/read reader :key-fn keyword))))

(defn- set-from-comma-separated
  [s]
  (set (map keyword
            (string/split s #"\s*,\s*"))))

(defn simplify-person
  "Filter and rename keys, convert values to be more natural in Clojure."
  [person]
  {:birth-year (:birth_year person)
   :height     (parse-long (:height person))
   :gender     (keyword (:gender person))
   :name       (:name person)
   :films      (:films person)
   :eye-color  (keyword (:eye_color person))
   :mass       (parse-long (:mass person))
   :hair-color (set-from-comma-separated (:hair_color person))
   :skin-color (set-from-comma-separated (:skin_color person))})


(for [doc    people-docs
      person (:results doc)]
  (simplify-person person))


(apply concat (for [doc people-docs]
                (map simplify-person (:results doc))))

(let [raw-persons (mapcat :results people-docs)
      persons     (map simplify-person raw-persons)
      just-mass   (keep :mass persons)
      total-mass  (apply + just-mass)]
  total-mass)

(let [persons (apply concat
                     (for [doc people-docs]
                       (map simplify-person (:results doc))))]
  (loop [result 0
         coll   persons]
    (if (some? coll)
      (recur (+ result (or (:mass (first coll)) 0))
             (next coll))
      result)))


(let [raw-persons (mapcat :results people-docs)
      persons     (map simplify-person raw-persons)]
  (loop [result 0
         coll   persons]
    (if (some? coll)
      (recur (+ result (or (:mass (first coll)) 0))
             (next coll))
      result)))


(def people (->> people-docs
                 (mapcat :results)
                 (map simplify-person)))

(let [raw-persons (mapcat :results people-docs)
      persons     (map simplify-person raw-persons)
      just-mass   (keep :mass persons)
      total-mass  (apply + just-mass)]
  total-mass)

(->> (filter #(= :blue (:eye-color %)) people)
     (map :name))

(->> people
     (mapcat (fn [person]
               (let [name (:name person)]
                 (map (fn [film]
                        {:name name
                         :film film})
                      (:films person)))))
     (reduce (fn [film->names film+name]
               (update film->names (:film film+name)
                       conj (:name film+name)))
             {}))

(let [person->film+names (fn [person]
                           (let [name (:name person)]
                             (map (fn [film]
                                    {:name name
                                     :film film})
                                  (:films person))))
      combine-film+names (fn [film->names film+name]
                           (update film->names (:film film+name)
                                   conj (:name film+name)))]
  (->> people
       (map person->film+names)
       (reduce into [])
       (reduce combine-film+names {})))

(let [person->film+names (fn [person]
                           (let [name (:name person)]
                             (map (fn [film]
                                    {:name name
                                     :film film})
                                  (:films person))))
      combine-film+names (fn [film->names film+name]
                           (update film->names (:film film+name)
                                   conj (:name film+name)))]
  (->> people
       (mapcat person->film+names)
       (reduce combine-film+names {})))

(let [person->film+names (fn [{:keys [name films]}]
                           (map (fn [film]
                                  {:name name
                                   :film film})
                                films))
      combine-film+names (fn [film->names {:keys [film name]}]
                           (update film->names film conj name))]
  (->> people
       (mapcat person->film+names)
       (reduce combine-film+names {})))

(let [person->film+names (fn [{:keys [name films]}]
                           (map #(vector name %) films))
      combine-film+names (fn [film->names [name film]]
                           (update film->names film conj name))]
  (->> people
       (mapcat person->film+names)
       (reduce combine-film+names {})))
