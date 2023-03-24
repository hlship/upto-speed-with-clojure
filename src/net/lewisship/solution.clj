(ns net.lewisship.solution
  (:require
    [net.lewisship.chess-board :as b :refer [new-board render-board render-board*]]
    [nextjournal.clerk :as clerk]))

(clerk/add-viewers! [b/chess-board-viewer])

;; Piece positions are prefixed by column and row (in chess notation, the column comes first, but is a letter).
;; Origin is the lower left corner.

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

(defn valid-position?
  [pos]
  (let [[col row] pos]
    (and (< 0 col 5)
      (< 0 row 5))))

(defn valid-move?
  "A move is valid if the to position is on the board, and there is a piece to capture
  at the to position."
  [board to]
  (and (valid-position? to)
    (get board to)))

(defn adjust-position
  [pos offset]
  (let [[col row] pos
        [col-delta row-delta] offset]
    [(+ col col-delta)
     (+ row row-delta)]))

(defn moves-for-pawn
  "Returns potential moves for a pawn."
  [from]
  ;; These functions will return some invalid moves that get filtered later.
  [(adjust-position from [-1 1])
   (adjust-position from [1 1])])

(defn moves-for-king
  [from]
  (for [col (range -1 2)
        row (range -1 2)
        :when (not (and (zero? col) (zero? row)))]
    (adjust-position from [col row])))

(defn moves-for-knight
  [from]
  (for [offset [[2 -1]
                [1 -2]
                [-1 -2]
                [-2 -1]
                [-2 1]
                [-1 2]
                [1 2]
                [2 1]]]
    (adjust-position from offset)))

(defn shoot-ray
  [board from offset]
  (loop [pos (adjust-position from offset)]

    (cond
      (not (valid-position? pos))
      nil

      (get board pos)
      pos

      :else
      (recur (adjust-position pos offset)))))

(defn moves-for-bishop
  [board pos]
  (keep #(shoot-ray board pos %) [[-1 1] [-1 -1] [1 1] [1 -1]]))

(defn moves-for-rook
  [board pos]
  (keep #(shoot-ray board pos %) [[0 1] [0 -1] [1 0] [-1 0]]))

(defn moves-for-queen
  [board pos]
  (concat
    (moves-for-bishop board pos)
    (moves-for-rook board pos)))

(clerk/example
  (moves-for-pawn [2 2])
  (moves-for-king [2 2])
  (moves-for-knight [2 2])
  (moves-for-bishop
    (new-board 1 1 :pawn
      1 4 :king
      4 4 :queen
      2 2 :bishop)
    [2 2])
  (moves-for-rook
    (new-board 1 1 :pawn
      1 4 :king
      1 3 :rook
      4 3 :queen)
    [1 3])

  )

(defn valid-moves-from-position
  "Returns valid moves from a position.  Returns a seq of moves (each move is a from position and a to position)."
  [board from]
  (let [potential-moves (case (get board from)
                          :pawn (moves-for-pawn from)
                          :king (moves-for-king from)
                          :knight (moves-for-knight from)
                          :rook (moves-for-rook board from)
                          :bishop (moves-for-bishop board from)
                          :queen (moves-for-queen board from))]
    (filter #(valid-move? board %) potential-moves)))

(defn legal-moves
  "Returns all legal moves from this board position, or nil if none."
  [board]
  (for [from (keys board)
        to   (valid-moves-from-position board from)]
    [from to]))


(clerk/example
  (apply-move (new-board 1 1 :pawn 2 2 :pawn)
    [[1 1] [2 2]])

  (b/describe-move (new-board 2 3 :pawn
                     3 4 :queen)
    [[2 3] [3 4]]))

(defn find-solution*
  [board prior-moves]
  (if (= 1 (count board))
    prior-moves
    (let [moves (legal-moves board)]
      (if-not (seq moves)
        nil
        ;; Depth first search: only one move (at most) will yield a solution
        ;; (assuming the puzzle is perfect with a single solution).
        (first (keep #(find-solution* (apply-move board %) (conj prior-moves %)) moves))))))

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
  (let [moves      (find-solution board)
        start-rows [[:div.mb-2 "Start:"]
                    (render-board* board)]]
    (loop [board board
           [move & more-moves] moves
           rows  start-rows]
      (if-not move
        (clerk/html (into [:div] rows))
        (let [board' (apply-move board move)]
          (recur board'
            more-moves
            (conj rows
              [:div.mt-5.mb-2 (b/describe-move board move) ":"]
              (render-board* board'))))))))

(present-solution (new-board
                    3 1 :knight
                    1 2 :bishop
                    2 2 :pawn
                    3 1 :knight
                    2 3 :bishop
                    3 3 :rook
                    3 4 :rook
                    4 4 :pawn))
