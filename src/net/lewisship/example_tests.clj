(ns net.lewisship.example-tests
  (:require [clojure.test :refer [deftest is testing are use-fixtures]]
            matcher-combinators.clj-test))

(deftest force-success
  (is (= true true))
  (is true)
  (is (not false))
  (is (even? 2))
  (is :anything))

(deftest force-failure
  (is (= false true))
  (is (= 0 1))
  (is (odd? 2))
  (is (not true)))

(def the-great-answer :out-of-memory-failure)

(deftest with-failure-message
  (is (= 42 the-great-answer)
      "DeepThought should respond correctly"))

(deftest expands-terms
  (let [value (* 3 3)]
    (is (even? value)))
  (is (string? (* 5 25))))

(deftest with-context
  (testing "addition"
    (is (= 2.0 (+ 1 1))
        "Numbers are numbers"))

  (testing "subtraction"
    (is (= 5.0 (- 7 2)))))

(deftest nested-contexts
  (testing "outer"
    (testing "middle"
      (testing "inner"
        (is (= Math/PI (/ 22 7)))))))

(deftest addition
  (are [expected x y]
    (= expected (+ x y))
    2 1 1

    3 2 1

    5 4 3))

(deftest thrown-failure
  (is (thrown? Exception
               :should-have-thrown-exception)))

(deftest thrown-mismatch
  (is (thrown? IllegalArgumentException
               (throw (RuntimeException. "not a match")))))

(deftest thrown-with-message
  (is (thrown-with-msg? Exception #"darth vader"
                        (throw (IllegalArgumentException. "annakin skywalker")))))

(deftest check-thrown-exception-info
  (when-let [e (is (thrown? Exception
                            (throw (ex-info "api failure" {:host "localhost"}))))]
    (is (= "api failure"
           (ex-message e)))
    (is (= {:host "0.0.0.0"}
           (ex-data e))))

  )
