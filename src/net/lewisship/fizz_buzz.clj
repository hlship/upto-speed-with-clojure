(ns net.lewisship.fizz-buzz
  (:require [nextjournal.clerk :as clerk]))

;; ## FizzBuzz

;;> Write a program that prints the numbers from 1 to 100. But for multiples of three print Fizz instead of the number
;;>  and for the multiples of five print Buzz. For numbers which are multiples of both three and five print FizzBuzz.


;; Since we're in Clerk, instead of printing, we'll just return a seq with the values and let Clerk do the printing.


;; This version has an explicit loop, it is almost procedural:

(loop [i      1
       result []]
  (if (= i 101)
    result
    (let [mult3? (zero? (mod i 3))
          mult5? (zero? (mod i 5))]
      (cond
        (and mult3? mult5?)
        (recur (inc i) (conj result "FizzBuzz"))

        mult3?
        (recur (inc i) (conj result "Fizz"))

        mult5?
        (recur (inc i) (conj result "Buzz"))

        :else
        (recur (inc i) (conj result i))))))

;; We can slim this down a bit:

(loop [i      1
       result []]
  (if (= i 101)
    result
    (let [mult3? (zero? (mod i 3))
          mult5? (zero? (mod i 5))
          term   (cond
                   (and mult3? mult5?)
                   "FizzBuzz"

                   mult3?
                   "Fizz"

                   mult5?
                   "Buzz"

                   :else
                   i)]
      (recur (inc i) (conj result term)))))


;; Getting closer; here we're using a `for` list comprehension:
;; `(range 1 101)` returns a seq between 1 (inclusive) and 101 (exclusive).

(for [i (range 1 101)
      :let [mult3? (zero? (mod i 3))
            mult5? (zero? (mod i 5))]]
  (cond
    (and mult5? mult3?)
    "FizzBuzz"

    mult3?
    "Fizz"

    mult5?
    "Buzz"

    :else
    i))

;; This is concise and maybe captures the original challenge the closest?

(for [i (range 1 101)
      :let [fizz (when (zero? (mod i 3))
                   "Fizz")
            buzz (when (zero? (mod i 5))
                   "Buzz")]]
  (if (or fizz buzz)
    (str fizz buzz)
    i))

;; You can rewrite a for to a map and it is often an improvement.

(map (fn [i]
       (let [fizz (when (zero? (mod i 3))
                    "Fizz")
             buzz (when (zero? (mod i 5))
                    "Buzz")]
         (if (or fizz buzz)
           (str fizz buzz)
           i)))
     (range 1 101))

;;  We can (cleverly?) get rid of `mod` checks by using `cycle`.

(map (fn [i fizz buzz]
       (if (or fizz buzz)
         (str fizz buzz)
         i))
     (range 1 101)
     (cycle [nil nil "Fizz"])
     (cycle [nil nil nil nil "Buzz"]))

;; If you really wanted to print the results, you would do so as:

^{::clerk/visibility {:code :hide}}
(clerk/code
  '(run! println (map ...)))

