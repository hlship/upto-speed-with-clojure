(ns net.lewisship.puzzle
  (:require [net.lewisship.chess-board :as b]
            [nextjournal.clerk :as clerk]))

^{::clerk/visibility {:result :hide}}
(clerk/add-viewers! [b/chess-board-viewer])

(clerk/example
  (b/new-board 3 3 :queen 3 4 :king 1 1 :pawn))

(def easy-board (b/new-board 2 2 :pawn
                             1 1 :pawn
                             1 3 :pawn))

(def pawn-moves
  [[-1 1]
   [+1 1]])

(defn find-targets-for-pawn [board position]
  (for [move pawn-moves]
    (mapv + position move))
  #_(let [[col row] position]
      [[(inc col) (dec row)]
       [(dec col) (dec row)]]))

(defn valid-position? [[col row]]
  (and (< 0 col 5)
       (< 0 row 5)))

(defn target-filter [board target-position]
  ;; Since it must be a capture, call to valid-position? is probably
  ;; not necessary.
  (and (valid-position? target-position)
       ;; Must be a capture:
       (contains? board target-position)))

(defn find-moves-for-piece [board position]
  (let [piece             (get board position)
        target-positions  (case piece
                            :pawn (find-targets-for-pawn board position))
        target-positions' (filter #(target-filter board %) target-positions)]
    ;; Convert to moves:
    (map vector (repeat position) target-positions')))

;; move [[fc fr] [tc tr]]

(defn find-moves [board]
  (reduce
    (fn [moves source-pos]
      (into moves (find-moves-for-piece board source-pos)))
    []
    (keys board)))


(defn apply-move
  [board move]
  (prn :board board :move move)
  (let [[source-pos target-pos] move
        piece (get board source-pos)]
    (-> board
        (dissoc source-pos)
        (assoc target-pos piece))))

(defn explore-solution-space [board moves]
  (if
    (= 1 (count board))
    moves
    (let [moves-from-here (find-moves board)]
      (if-not (seq moves-from-here)
        nil
        (some identity
              (map (fn [move]
                        (explore-solution-space
                          (apply-move board move)
                          (conj moves move)))
                      moves-from-here))))))

(defn find-solution [board]
  (explore-solution-space board []))

(defn present-solution
  [board]
  (let [moves      (find-solution board)
        start-rows [[:div.mb-2 "Start:"]
                    (b/render-board* board)]]
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
                       (b/render-board* board'))))))))

(find-moves easy-board)
(present-solution easy-board)
#_(for [move (find-moves easy-board)]
    (show-move easy-board move))

