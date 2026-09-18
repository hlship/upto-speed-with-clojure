(ns net.lewisship.advent23.day5
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [net.lewisship.chess-board :as chess-board]))



(defn add-range-to-formula
  [formula line]
  (let [[dest-start source-start range-length] (->> line
                                                    (re-seq #"\d+")
                                                    (map parse-long))]
    (update formula :ranges conj
            {:dest-start   dest-start
             :source-start source-start
             :range-length range-length})))

(defn parse-formulas
  [lines]
  (loop [[line & more-lines] lines
         formula nil
         result []]
    (cond
      (nil? line)
      (if formula
        (conj result formula)
        result)

      (string/blank? line)
      (recur more-lines formula result)

      (string/includes? line "map:")
      (recur more-lines
             {:name   (-> line (string/split #"\s") first keyword)
              :ranges []}
             (if formula
               (conj result formula)
               result))

      :else
      (recur more-lines
             (add-range-to-formula formula line)
             result))))

(defn parse-data
  [lines]
  (let [[seed-line & formula-lines] lines]
    {:seeds    (->> seed-line
                    (re-seq #"\d+")
                    (map parse-long))
     :formulas (parse-formulas formula-lines)}))


(defn part1
  [file]
  (let [lines (-> file
                  io/resource
                  slurp
                  string/split-lines)
        data (parse-data lines)]
    data))

(comment

  (part1 "data/day5-sample.txt")

  )
