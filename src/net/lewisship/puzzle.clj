(ns net.lewisship.puzzle
  (:require [net.lewisship.chess-board :as b]
            [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:result :hide}}
(clerk/add-viewers! [b/chess-board-viewer])

(clerk/example
  (b/new-board 3 3 :queen 3 4 :king 1 1 :pawn))
