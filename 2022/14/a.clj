(ns aoc.2022.14.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9
  ")

(def input (aoc.2022/read-input sample-input))

(defn parse-rock [rock]
  (->> rock
       (re-seq #"(\d+),(\d+)")
       (map (fn [[_ x y]] [(Integer/parseInt x) (Integer/parseInt y)]))))

(def rock-paths (->> input
                     str/trim
                     str/split-lines
                     (map str/trim)
                     (map parse-rock)))

(defn solids [[[from-x from-y] [to-x to-y]]]
  (let [min-x (min from-x to-x)
        max-x (max from-x to-x)
        min-y (min from-y to-y)
        max-y (max from-y to-y)]
    (if (= min-x max-x)
      (for [x [min-x], y (range min-y (inc max-y))] [x y])
      (for [y [min-y], x (range min-x (inc max-x))] [x y]))))

(def cave (->> rock-paths
               (mapcat (fn [rock] (map solids (partition 2 1 rock))))
               (apply concat)
               (map #(identity [% "#"]))
               (into {})))

(def rock-bottom (->> rock-paths
                      (apply concat)
                      (map last)
                      (apply max)))
(def sand-origin [500 0])

(defn pour
  ([cave] (pour cave sand-origin))
  ([cave [x y]]
   (let [below [x (inc y)]
         left [(dec x) (inc y)]
         right [(inc x) (inc y)]]
     (cond
       (> y rock-bottom) cave
       (not (cave below)) (recur cave below)
       (not (cave left)) (recur cave left)
       (not (cave right)) (recur cave right)
       :else (pour (assoc cave [x y] "o"))))))

(pr (-> cave
        pour
        vals
        frequencies
        (get "o")))
