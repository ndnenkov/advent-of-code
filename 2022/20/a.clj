(ns aoc.2022.20.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
1
2
-3
3
-2
0
4
  ")

(def input (aoc.2022/read-input sample-input))

(def numbers (->> input str/trim str/split-lines (map #(Integer/parseInt %))))
(def number-identities (map #(identity {:number %, :mixed false}) numbers))

(defn insert-at [coll index element]
  (let [[before after] (split-at index coll)]
    (concat before [element] after)))

(defn mix [number-identities]
  (if (every? #(:mixed %) number-identities) number-identities
      (let [[index number-identity] (->> number-identities (keep-indexed #(when-not (:mixed %2) [%1 %2])) first)
            before (take index number-identities)
            after (drop (inc index) number-identities)
            mixed-number (merge number-identity {:mixed true})
            mix-at (mod (+ index (:number number-identity)) (dec (count number-identities)))
            wrapped-mix-at (if (zero? mix-at) (count number-identities) mix-at)]
        (recur (insert-at (concat before after) wrapped-mix-at mixed-number)))))

(def mixed-numbers (->> number-identities mix (map :number)))

(def i0 (first (keep-indexed #(when (zero? %2) %1) mixed-numbers)))
(def i1000 (mod (+ i0 1000) (count numbers)))
(def i2000 (mod (+ i0 2000) (count numbers)))
(def i3000 (mod (+ i0 3000) (count numbers)))

(pr (reduce +
            [(nth mixed-numbers i1000)
             (nth mixed-numbers i2000)
             (nth mixed-numbers i3000)]))
