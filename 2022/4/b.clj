(ns aoc.2022.4.b)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

(load-file "read_input.clj")

(def sample-input
  "
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8
  ")

(def input (aoc.2022/read-input sample-input))

(defn parse-assignment [assignment]
  (let [[_ first-from first-to second-from second-to] (re-find #"(\d+)-(\d+),(\d+)-(\d+)" assignment)]
    [[(Integer/parseInt first-from) (Integer/parseInt first-to)]
     [(Integer/parseInt second-from) (Integer/parseInt second-to)]]))

(def assignments (->> input str/trim str/split-lines (map parse-assignment)))

(defn overlap? [first-elf second-elf]
  (let [first-section (set (range (first first-elf) (inc (last first-elf))))
        second-section (set (range (first second-elf) (inc (last second-elf))))]

    (not (empty? (set/intersection first-section second-section)))))

(def overlapping-count (->> assignments
                            (filter #(apply overlap? %))
                            count))

(pr overlapping-count)
