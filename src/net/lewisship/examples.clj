
;; # Some Examples

(ns net.lewisship.examples
  (:require [nextjournal.clerk :as clerk]))

;; ## Define person map to work on

(def person {:first-name "John"
             :last-name  "Smith"
             :age        32})

;; ## assoc - associate key and value into map

(assoc person :title "Dr.")

person

;; ## dissoc - disassociate (remove) keys

(dissoc person :age)

(contains? (assoc person :age nil) :age)

(contains? (dissoc person :age) :age)

(dissoc person :first-name :age)

;; ## Getting values

(get person :age)

(get person :title "None")

(:age person)

(:title person)

(:title person "None")

;; ## keys and vals

(keys person)

(vals person)

;; ## update - apply function to value at key

(assoc person :age (inc (:age person)))

(update person :age inc)

(str 32 " years")

(update person :age str " years")

;; ## assoc-in

(def customer {:id         3717737
               :first-name "John"
               :last-name  "Smith"
               :address    {:street "14 Elm St."
                            :city   "Boring"
                            :state  "OR"
                            :zip    "97009"}})

(assoc-in customer [:address :phone] "503-555-1212")

customer

(update-in customer [:address :zip] str "-12345")

;; ## cons and conj - add to lists

(def names '("mick" "john" "christine" "lindsey" "stevie"))

(cons "peter" names)

(conj names :FIRST)

(conj names "peter" "jeremy" "bob")

(conj [:kirk :bones] :spock :sulu)


;; ## first, rest, next -- navigate sequential structuers

(first names)

(rest names)

(next names)

(rest '(:only))

(next '(:only))

;; ## take, drop

(take 2 names)

(drop 2 names)

(drop 2 "stevie")

;; ## Basic functions

(defn avg
  [value1 value2]
  (/ (+ value1 value2) 2))

(clerk/example
  (avg 10 20)
  (avg 10M 20M)
  (avg 4.0 3.5)
  (avg 2 9))

(defn avg
  "Returns the average of its inputs."
  ([value1 value2]
   (/ (+ value1 value2) 2))
  ([value1 value2 value3]
   (/ (+ value1 value2 value3) 3)))

(clerk/example
  (avg 3 5)
  (avg 88 87 73)
  (float (avg 88 87 73)))

;; ## apply - call a function w/ seq of args

(apply + [50 5])

(apply + 3 7 [50 5])

(defn avg
  "Computes the average of a list of numbers"
  [& values]
  (/ (apply + values)
    (count values)))

(clerk/example
  (avg 5 10 15 20 25)
  (float (avg 81 75 73 84)))

;; ## anonymous and inline functions

(def square (fn [x] (* x x)))

(square 5)

(def cube #(* % % %))

(cube 7)

(def mult #(* %1 %2))

(mult 4 7)

;; ## and, or

(clerk/example
  (and 1 2 "keep going" :fred)
  (and 1 2 false :fred)
  (and 1 2 nil :fred)
  (and)
  (and false (throw (RuntimeException.))))

(clerk/example
  (or 1 2 3)
  (or false nil :pick-me!)
  (or (:given-name person) "NFN"))

;; ## cond

(defn weather
  [temp]
  (cond
    (< temp 60) "Chilly"

    (< temp 80) (str temp " is just right")

    (< temp 100) "Heatwave"

    (< temp 140) "Life Threatening"

    :else
    "Deadly Hot"))

(clerk/example
  (weather 40)
  (weather 72)
  (weather 142))

;; ## let - local symbols

(let [width 100
      height 50
      depth 5
      volume (* width height depth)]
  (println "Volume" volume)
  {:width width :height height :depth depth
   :volume volume})

; ## for - implicit lazy looping

; match each suit with each rank

(for [suit [:spades :hearts :clubs :diamonds]
      rank [:jack :queen :king]]
  [suit rank])


; can reference earlier values

(for [x (range 0 6)
      y (range 0 (inc x))]
  [x y])

; :when qualifier (only continue when true)

(for [x (range 0 6)
      :when (even? x)
      y (range 0 (inc x))
      :when (odd? y)]
  [x y])


;; ## looping via recursion

(defn stars
  [n]
  (if (zero? n)
    ""
    (str "*" (stars (dec n)))))

(stars 5)

;; ## loop/recur

(defn stars
  [n]
  (loop [result    ""
         remaining n]
    (if (zero? remaining)
      result
      (recur (str result "*")
        (dec remaining)))))

(stars 5)

;; But really ...

(apply str (repeat 5 "*"))

(defn reverse-list
  [input]
  (loop [remaining input
         result ()]
    (if remaining
      (recur (next remaining)
        (cons (first remaining) result))
      result)))

(reverse-list [5 4 3 2 1])

;; # Destructuring

(let [[x y z :as input] [1 2]]
  {:x x :y y :z z
   :input input})

;; The :keys key can be name-spaced to indicate that the corresponding keys are also name-spaced.

(let [{::keys [x y z]
       :or {y 0 z 0}
       :as input} {::x 1 ::y 1}]
  {:x x :y y :z z :input input})

(let [[x y z :as input] [1 2 3 4 5]]
  {:x x :y y :z z
   :input input})



