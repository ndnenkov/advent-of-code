(ns aoc.2022.13.b)

(load-file "read_input.clj")

(def sample-input
  "
[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]
  ")

(def input (aoc.2022/read-input sample-input))

(def packets (load-string (str "[" input "]")))
(def start-packet [[2]])
(def end-packet [[6]])

(defn compare-packets [x y]
  (case [(number? x) (number? y)]
    [true true] (compare x y)
    [true false] (recur [x] y)
    [false true] (recur x [y])
    (if (some empty? [x y]) (compare (count x) (count y))
        (let [head-comparison (compare-packets (first x) (first y))]
          (if-not (zero? head-comparison) head-comparison (recur (rest x) (rest y)))))))

(pr (->> (into packets [start-packet end-packet])
         (sort-by identity #(compare-packets %1 %2))
         (keep-indexed #(when (#{start-packet end-packet} %2) (inc %1)))
         (reduce *)))
