(ns aoc.2022.18.a)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

(load-file "read_input.clj")

(def sample-input
  "
2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5
  ")

(def input (aoc.2022/read-input sample-input))

(def cubes (->> input
                str/trim
                str/split-lines
                (map #(str "[" % "]"))
                (map load-string)
                set))

(defn neighbours [[x y z :as cube]]
  #{[(inc x) y z] [(dec x) y z] [x (inc y) z] [x (dec y) z] [x y (inc z)] [x y (dec z)]})

(defn exposed-sides [cubes]
  (reduce +
          (for [cube cubes]
            (- 6 (-> cube neighbours (set/intersection cubes) count)))))

(pr (exposed-sides cubes))
