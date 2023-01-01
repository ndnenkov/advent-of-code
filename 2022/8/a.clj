(ns aoc.2022.8.a)

(require '[clojure.string :as str])
(require '[clojure.math.combinatorics :as combo])
(require '[clojure.math.numeric-tower :as num])

(load-file "read_input.clj")

(def sample-input
  "
30373
25512
65332
33549
35390
  ")

(def input (aoc.2022/read-input sample-input))

(def trees (->> input
                str/trim
                (#(str/split % #""))
                (filter #(not= % "\n"))
                (map #(Integer/parseInt %))))
(def side-size (-> trees count num/sqrt))
(def grid (partition side-size trees))

(defn index-of [predicate collection]
  (->> collection (keep-indexed #(when (predicate %2) %1)) first))

(defn hidden-past [tree-line]
  (->> tree-line butlast (partition 2 1) (index-of #(apply >= %)) inc))

(defn hidden-past-reversed [tree-line]
  (- (count tree-line) (hidden-past (reverse tree-line)) 1))

(def all-indices (-> side-size range (combo/selections 2)))

(defn rolling-max [collection] (->> collection (reductions max) (into [##-Inf]) butlast))
(defn reverse-rolling-max [collection] (->> collection reverse (reductions max) vec butlast (#(into % [##-Inf])) reverse))

(defn filter-horizontal
  ([grid indices] (filter-horizontal grid 0 indices))
  ([grid index indices] (if (= (count grid) index) indices
                            (let [line (nth grid index)
                                  max-left (rolling-max line)
                                  max-right (reverse-rolling-max line)]
                              (recur grid (inc index)
                                     (filter (fn [[x y]]
                                               (or
                                                (not= x index)
                                                (and (<= (nth line y) (nth max-left y))
                                                     (<= (nth line y) (nth max-right y)))))
                                             indices))))))
(defn filter-vertical
  ([grid indices] (filter-vertical (apply mapv vector grid) 0 indices))
  ([grid index indices] (if (= (count grid) index) indices
                            (let [line (nth grid index)
                                  max-left (rolling-max line)
                                  max-right (reverse-rolling-max line)]
                              (recur grid (inc index)
                                     (filter (fn [[y x]]
                                               (or
                                                (not= x index)
                                                (and (<= (nth line y) (nth max-left y))
                                                     (<= (nth line y) (nth max-right y)))))
                                             indices))))))

(def a (filter-horizontal grid all-indices))
(def b (filter-vertical grid a))

(pr (- (count all-indices) (count b)))
