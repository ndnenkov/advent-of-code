(ns aoc.2022.3.a)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

(load-file "read_input.clj")

(def sample-input
  "
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
  ")

(def input (aoc.2022/read-input sample-input))

(def bags (->> input
               str/trim
               str/split-lines
               (map #(split-at (/ (count %) 2) (seq %)))))

(defn priority [item-type]
  (let [item-ascii (int item-type)
        a-ascii (int \a)
        z-ascii (int \z)
        A-ascii (int \A)
        Z-ascii (int \Z)]
    (cond
      (<= a-ascii item-ascii z-ascii) (inc (- item-ascii a-ascii))
      (<= A-ascii item-ascii Z-ascii) (+ 27 (- item-ascii A-ascii)))))

(defn repeating-item [first-compartment second-compartment]
  (first (set/intersection (set first-compartment) (set second-compartment))))

(def total-priority (->> bags
                         (map #(apply repeating-item %))
                         (map priority)
                         (reduce +)))

(pr total-priority)
