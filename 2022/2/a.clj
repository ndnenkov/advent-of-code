(ns aoc.2022.2.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
A Y
B X
C Z
  ")

(def input (aoc.2022/read-input sample-input))

(def rounds (->> input
                 str/trim
                 str/split-lines
                 (map #(str/split % #"\s"))))

(def enemy-moves {"A" :rock, "B" :paper, "C" :scissors})
(def my-moves {"X" :rock, "Y" :paper, "Z" :scissors})

(def counters {:rock :scissors, :scissors :paper, :paper :rock})
(def move-scoring {:rock 1, :paper 2, :scissors 3})

(defn play-round [enemy, my]
  (let [enemy-move (enemy-moves enemy)
        my-move (my-moves my)
        move-score (move-scoring my-move)
        outcome-score (cond
                        (= (counters my-move) enemy-move) 6
                        (= (counters enemy-move) my-move) 0
                        :else 3)]
    (+ move-score outcome-score)))

(def total-score (->> rounds
                      (map #(apply play-round %))
                      (reduce +)))

(pr total-score)
