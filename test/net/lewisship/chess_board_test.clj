(ns net.lewisship.chess-board-test
  (:require
    [clojure.test :refer [deftest is]]
    [net.lewisship.chess-board :as board]))

(deftest can-describe-a-basic-move
  (let [board (board/new-board 1 1 :pawn
                               2 2 :knight
                               3 2 :queen)
        move [[1 1] [2 2]]]
    (is (= "Pawn takes Knight"
           (board/describe-move board move)))))

(deftest can-describe-a-move-unambiguously-when-multiple-of-that-type
  (let [board (board/new-board 3 1 :pawn
                               1 1 :pawn
                               2 2 :knight
                               3 2 :queen)
        move [[1 1] [2 2]]]
    (is (= (board/describe-move board move)
           "Pawn at 1,1 takes Knight"))))
