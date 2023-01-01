(ns aoc.2022.2.b)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

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
(def strategies {"X" :lose, "Y" :draw, "Z" :win})

(def counters {:rock :scissors, :scissors :paper, :paper :rock})
(def weaknesses (set/map-invert counters))

(def move-scoring {:rock 1, :paper 2, :scissors 3})
(def outcome-scoring {:lose 0, :draw 3, :win 6})

(defn play-round [enemy, strategy]
  (let [enemy-move (enemy-moves enemy)
        outcome (strategies strategy)
        my-move (case outcome
                  :lose (counters enemy-move)
                  :draw enemy-move
                  :win (weaknesses enemy-move))
        move-score (move-scoring my-move)
        outcome-score (outcome-scoring outcome)]
    (+ move-score outcome-score)))

(def total-score
  (->> rounds
       (map #(apply play-round %))
       (reduce +)))

(pr total-score)
