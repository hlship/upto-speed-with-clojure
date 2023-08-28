(ns net.lewisship.macro-examples
  (:require [net.lewisship.macros :refer [time+]]))

(defn factorial
  [n]
  (if (zero? n)
    1
    (* n (factorial (dec n)))))

(factorial 10)

(macroexpand-1 '(time+ (+ 3 5)))

(time+ (+ 3 5))

(time+ (factorial 20))
