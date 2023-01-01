(ns aoc.2022.6.a)

(load-file "read_input.clj")

(def sample-input
  "
mjqjpqmgbljsphdztnvjfqwrcgsmlb
  ")

(def input (aoc.2022/read-input sample-input))

(def marker-size 4)
(def start-marker-index (->> input
                             seq
                             (partition marker-size 1)
                             (map-indexed #(when (-> %2 set count (= marker-size)) %1))
                             (remove nil?)
                             first))

(pr (+ marker-size start-marker-index))
