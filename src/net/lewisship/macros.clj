(ns net.lewisship.macros
  (:require
    [clojure.pprint :refer [pprint]]))

(defmacro cond-let
  [& clauses]
  (cond
    (not (even? (count clauses)))
    (throw (ex-info "cond-let macro requires an even number of forms"
                    {:form &form
                     :meta (meta &form)}))

    (empty? clauses)
    nil

    :else
    (let [[test exp & more-clauses] clauses]
      (if (= :let test)
        `(let ~exp (cond-let ~@more-clauses))
        `(if ~test
           ~exp
           (cond-let ~@more-clauses))))))


(defn time+*
  [context f]
  (let [start-nanos   (System/nanoTime)
        result        (f)
        elapsed-nanos (- (System/nanoTime) start-nanos)]
    (pprint (assoc context
                   :result result
                   :elapsed-ms (/ elapsed-nanos 1000000.)))
    result))

(defmacro time+
  [& body]
  (let [context (assoc (meta &form)
                       :ns (ns-name *ns*)
                       :body body)]
    `(time+* '~context (fn [] ~@body))))


(defmacro time+
  [& body]
  (let [context (assoc (meta &form)
                       :ns (ns-name *ns*)
                       :body body)]
    `(let [start-nanos#   (System/nanoTime)
           result#        (do ~@body)
           elapsed-nanos# (- (System/nanoTime) start-nanos#)]
       (pprint (assoc '~context
                      :result result#
                      :elapsed-ms (/ elapsed-nanos# 1000000.)))
       result#)))

(comment

  (macroexpand-1 '(cond-let
                    :let [a 1]

                    false nil

                    :let [b 2]

                    :else
                    (+ a b))))
