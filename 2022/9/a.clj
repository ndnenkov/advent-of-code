(ns aoc.2022.9.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
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
    (= (- head-x tail-x) 2) [(dec head-x) head-y]
    (= (- tail-x head-x) 2) [(dec tail-x) head-y]
    (= (- head-y tail-y) 2) [head-x (dec head-y)]
    (= (- tail-y head-y) 2) [head-x (dec tail-y)]
    :else [tail-x tail-y]))

(defn tail-path
  ([instructions] (tail-path instructions [0, 0] [0, 0] [[0, 0]]))
  ([instructions head tail path]
   (if (empty? instructions) path
       (let [instruction (first instructions)
             remaining-instructions (rest instructions)
             direction (first instruction)
             times (last instruction)
             new-instructions (if (= times 1) remaining-instructions (into [[direction (dec times)]] remaining-instructions))
             new-head (move-head head direction)
             new-tail (follow new-head tail)]
         (recur new-instructions new-head new-tail (conj path new-tail))))))

(pr (-> instructions tail-path distinct count))
