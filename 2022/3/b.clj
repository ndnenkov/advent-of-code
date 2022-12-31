(ns aoc.2022.3.b)

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

(def elf-triplets (->> input
                       str/trim
                       str/split-lines
                       (map seq)
                       (partition 3)))

(defn priority [item-type]
  (let [item-ascii (int item-type)
        a-ascii (int \a)
        z-ascii (int \z)
        A-ascii (int \A)
        Z-ascii (int \Z)]
    (cond
      (<= a-ascii item-ascii z-ascii) (inc (- item-ascii a-ascii))
      (<= A-ascii item-ascii Z-ascii) (+ 27 (- item-ascii A-ascii)))))

(defn repeating-item [first-compartment second-compartment third-compartment]
  (first (set/intersection (set first-compartment) (set second-compartment) (set third-compartment))))

(def total-priority
  (->> elf-triplets
       (map #(apply repeating-item %))
       (map priority)
       (reduce +)))

(pr total-priority)
