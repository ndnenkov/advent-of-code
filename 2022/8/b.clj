(ns aoc.2022.8.b)

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

(defn transpose [matrix] (apply mapv vector matrix))
(defn rotate-cw [matrix] (->> matrix transpose (mapv reverse)))
(defn rotate-ccw [matrix] (->> matrix rotate-cw rotate-cw rotate-cw))

(def all-indices (-> side-size range (combo/selections 2)))

(defn count-lazer [lazer tree] (let [stopped-lazer (take-while #(> tree %) (reverse lazer))]
                                 (+
                                  (count stopped-lazer)
                                  (if (= (count stopped-lazer) (count lazer)) 0 1))))

(defn useful-reductions [function collection]
  (->> collection count inc range rest (map #(take % collection)) (map function)))

(defn rolling-view [collection] (useful-reductions (fn [sublist] (count-lazer (butlast sublist) (last sublist))) collection))

(def left-to-right (map rolling-view grid))
(def bottom-to-top (->> grid rotate-cw (map rolling-view) rotate-ccw))
(def top-to-bottom (->> grid (map reverse) (map rolling-view) (map reverse)))
(def right-to-left (->> grid transpose (map rolling-view) transpose))

(def scenic-scores (->> [left-to-right bottom-to-top top-to-bottom right-to-left]
                        (map flatten)
                        (apply map vector)
                        (map #(reduce * %))))
(pr (apply max scenic-scores))
