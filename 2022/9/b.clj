(ns aoc.2022.9.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20
  ")

(def input (aoc.2022/read-input sample-input))

(def instructions (->> input
                       str/trim
                       str/split-lines
                       (map str/trim)
                       (map #(str/split % #"\s"))
                       (map (fn [[direction times]] (list direction (Integer/parseInt times))))))

(defn move-head [[x y] direction]
  (case direction
    "R" [(inc x) y]
    "L" [(dec x) y]
    "U" [x (inc y)]
    "D" [x (dec y)]))

(defn follow [[head-x head-y] [tail-x tail-y]]
  (cond
    (<= (max (abs (- head-x tail-x)) (abs (- head-y tail-y))) 1) [tail-x tail-y]
    (= (+ (abs (- head-x tail-x)) (abs (- head-y tail-y))) 4) [(/ (+ head-x tail-x) 2) (/ (+ head-y tail-y) 2)]
    (= (- head-x tail-x) 2) [(dec head-x) head-y]
    (= (- tail-x head-x) 2) [(dec tail-x) head-y]
    (= (- head-y tail-y) 2) [head-x (dec head-y)]
    (= (- tail-y head-y) 2) [head-x (dec tail-y)]))

(defn chain-follow [head knots]
  (let [new-head (follow head (first knots))
        new-knots (rest knots)]
    (if (empty? new-knots) [new-head]
        (into [new-head] (chain-follow new-head new-knots)))))

(defn tail-path
  ([instructions] (tail-path instructions [0 0] (replicate 9 [0 0]) [[0 0]]))
  ([instructions head knots path]
   (if (empty? instructions) path
       (let [instruction (first instructions)
             remaining-instructions (rest instructions)
             direction (first instruction)
             times (last instruction)
             new-instructions (if (= times 1) remaining-instructions (into [[direction (dec times)]] remaining-instructions))
             new-head (move-head head direction)
             new-knots (chain-follow new-head knots)]
         (recur new-instructions new-head new-knots (conj path (last new-knots)))))))

(pr (-> instructions tail-path distinct count))
