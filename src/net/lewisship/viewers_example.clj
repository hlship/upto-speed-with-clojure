(ns net.lewisship.viewers-example
  {:nextjournal.clerk/auto-expand-results? true}
  (:require [net.lewisship.chess-board :as b]
            [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:result :hide}}
(clerk/add-viewers! [b/chess-board-viewer])

(b/new-board 1 2 :pawn
             2 3 :bishop
             1 4 :knight)

