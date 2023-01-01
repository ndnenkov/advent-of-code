(ns aoc.2022.18.b)

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

(def solid-cubes (->> input
                      str/trim
                      str/split-lines
                      (map #(str "[" % "]"))
                      (map load-string)
                      set))

(defn neighbours [[x y z]]
  #{[(inc x) y z] [(dec x) y z] [x (inc y) z] [x (dec y) z] [x y (inc z)] [x y (dec z)]})

(defn exposed-sides [cubes]
  (reduce +
          (for [cube cubes]
            (- 6 (-> cube neighbours (set/intersection cubes) count)))))

(def bounds
  {:min-x (->> solid-cubes (map first) (apply min))
   :max-x (->> solid-cubes (map first) (apply max))
   :min-y (->> solid-cubes (map second) (apply min))
   :max-y (->> solid-cubes (map second) (apply max))
   :min-z (->> solid-cubes (map last) (apply min))
   :max-z (->> solid-cubes (map last) (apply max))})

(def all-cubes
  (set
   (for [x (range (:min-x bounds) (inc (:max-x bounds)))
         y (range (:min-y bounds) (inc (:max-y bounds)))
         z (range (:min-z bounds) (inc (:max-z bounds)))]
     [x y z])))

(def inside-bounds? all-cubes)

(def empty-cubes (set/difference all-cubes solid-cubes))

(def empty-boundary-cubes
  (set
   (filter
    (fn [[x y z]]
      (or
       (#{(:min-x bounds) (:max-x bounds)} x)
       (#{(:min-y bounds) (:max-y bounds)} y)
       (#{(:min-z bounds) (:max-z bounds)} z)))
    empty-cubes)))

(defn spread-infection
  ([exposed immune] (spread-infection exposed #{} immune))
  ([exposed infected immune]
   (let [patient (first exposed)]
     (if-not patient
       infected
       (recur
        (-> patient
            neighbours
            (set/intersection inside-bounds?)
            (set/difference infected)
            (set/difference immune)
            (set/union exposed)
            (disj patient))
        (conj infected patient)
        immune)))))

(def filled-with-air (spread-infection empty-boundary-cubes solid-cubes))

(pr
 (->> filled-with-air
      (set/difference all-cubes)
      exposed-sides))
