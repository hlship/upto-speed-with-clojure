(ns net.lewisship.chess-board
  "Renders a chess board from a chess position map."
  (:require [clojure.string :as string]
            [nextjournal.clerk :as clerk]))

(defn render-board*
  [board]
  [:div.grid.grid-cols-4.gap-0.w-fit
   (for [row (range 1 5)
         col (range 1 5)
         :let [key        [col (- 5 row)]
               color      (if (odd? (+ row col))
                            "bg-black"
                            "bg-red-600")
               piece      (get board key)
               image-path (if (#{:bishop :king :knight :pawn :queen :rook} piece)
                            (format "assets/chess_%s.png" (name piece))
                            "assets/blank.png")]]
     [:div.w-fit.h-fit {:class color}
      (clerk/image image-path)])])

(defn render-board [board]
  (clerk/html (render-board* board)))

(defn new-board [& setup]
  (assert (= 0 (mod (count setup) 3))
    "Must supply col row piece triplets")
  (with-meta
    (reduce (fn [m [col row piece]]
              (assoc m [col row] piece))
      {}
      (partition 3 setup))
    {::board? true}))

(def chess-board-viewer
  {:name `chess-board-viewer
   :pred #(-> % meta ::board?)
   :transform-fn (clerk/update-val render-board)})

(clerk/add-viewers! [chess-board-viewer])


(new-board
  1 1 :pawn
  2 2 :knight
  3 2 :queen)


(defn describe-move
  "Describes a move in terms of which piece takes which other piece, e.g. \"Knight takes king\"."
  [board move]
  (let [[from to] move
        freqs      (frequencies (vals board))
        piece-name (fn [pos]
                     (let [kind (get board pos)
                           [col row] pos]
                       (if (= 1 (get freqs kind))
                         (name kind)
                         (format "%s at %d,%d"
                           (name kind) col row))))]
    (format "%s takes %s"
      (string/capitalize (piece-name from))
      (piece-name to))))



