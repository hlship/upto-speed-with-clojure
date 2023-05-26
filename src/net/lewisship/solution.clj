(ns net.lewisship.solution
  (:require
    [clojure.string :as string]
    [net.lewisship.chess-board :as b :refer [new-board render-board render-board*]]
    [net.lewisship.trace :refer [trace]]
    [nextjournal.clerk :as clerk]
    [nextjournal.clerk.viewer :as v]))

(clerk/add-viewers! [b/chess-board-viewer])

;; Piece positions are prefixed by column and row (in chess notation, the column comes first, but is a letter):

(def simple-board (new-board 2 1 :pawn
                    1 2 :pawn))

(defn apply-move
  "Applies a known valid move to a board, returning a new board state.
  Move is a vector of from position and to position."
  [board move]
  (let [[from to] move
        piece (get board from)]
    (-> board
      (dissoc from)
      (assoc to piece))))

(clerk/example
  (apply-move simple-board [[2 1] [1 2]]))

(defn valid-move?
  "A move is valid if the to position is on the board, and there is a piece to capture
  at the to position."
  [board to]
  (let [[to-col to-row] to]
    (and (< 0 to-col 5)
      (< 0 to-row 5)
      (get board to))))

(defn adjust-position
  [pos col-delta row-delta]
  (let [[col row] pos]
    [(+ col col-delta)
     (+ row row-delta)]))

(defn moves-for-pawn
  "Returns potential moves for a pawn."
  [from]
  ;; These functions will return some invalid moves that get filtered later.
  [(adjust-position from -1 1)
   (adjust-position from 1 1)])

(defn valid-moves-from-position
  "Returns valid moves from a position.  Returns a seq of moves (each move is a from position and a to position)."
  [board from]
  (let [potential-moves (case (get board from)
                          :pawn (moves-for-pawn from))]
    (filter #(valid-move? board %) potential-moves)))

(defn possible-moves
  [board]
  (for [from (keys board)
        to   (valid-moves-from-position board from)]
    [from to]))

(defn describe-move
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
    (format "%s takes %s" (piece-name from) (piece-name to))))

(clerk/example
  (apply-move (new-board 1 1 :pawn 2 2 :pawn)
    [[1 1] [2 2]])

  (describe-move (new-board 2 3 :pawn
                            3 4 :queen)
                    [[2 3] [3 4]]))

(defn find-solution*
  [board prior-moves]
  (if (= 1 (count board))
    prior-moves
    (let [valid-moves (possible-moves board)]
      (if-not valid-moves
        nil
        ;; Depth first search: only one move (at most) will yield a solution.
        (first (keep #(find-solution* (apply-move board %) (conj prior-moves %)) valid-moves))))))

(defn find-solution
  "Given an initial board state, returns a sequence of moves that result in a final board state (single piece left)."
  [board]
  (find-solution* board []))

(clerk/example
  (find-solution (new-board 1 1 :pawn))

  (find-solution (new-board 1 1 :pawn
                             2 2 :pawn))

  (find-solution (new-board 1 1 :pawn
                   2 2 :pawn
                   1 3 :pawn))
  )

(defn present-solution
  [board]
  (let [moves (find-solution board)
        start-rows  [[:div.mb-2 "Start:"]
               (b/render-board* board)]]
    (loop [board board
           [move & more-moves] moves
           rows start-rows]
      (if-not move
        (clerk/html (into [:div] rows))
        (let [board' (apply-move board move)]
          (recur board'
            more-moves
            (conj rows
              [:div.mt-5.mb-2 (string/capitalize (describe-move board move)) ":"]
              (b/render-board* board'))))))))


(present-solution (new-board
                    1 1 :pawn
                    2 2 :pawn
                    1 3 :pawn))
