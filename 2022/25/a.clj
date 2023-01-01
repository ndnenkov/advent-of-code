(ns aoc.2022.25.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
1=-0-2
12111
2=0=
21
2=01
111
20012
112
1=-1=
1-12
12
1=
122
  ")

(def input (aoc.2022/read-input sample-input))

(def snafus (->> input
                 str/trim
                 str/split-lines
                 (mapv str/trim)
                 (mapv seq)))

(def sums
  {[\2 \2] [\- \1]
   [\2 \1] [\= \1]
   [\2 \0] [\2 \0]
   [\2 \-] [\1 \0]
   [\2 \=] [\0 \0]
   [\1 \1] [\2 \0]
   [\1 \0] [\1 \0]
   [\1 \-] [\0 \0]
   [\1 \=] [\- \0]
   [\0 \0] [\0 \0]
   [\0 \-] [\- \0]
   [\0 \=] [\= \0]
   [\- \-] [\= \0]
   [\- \=] [\2 \-]
   [\= \=] [\1 \-]})

(defn add-digits [n-digit m-digit]
  (let [n (or n-digit \0)
        m (or m-digit \0)]
    (or (sums [n m]) (sums [m n]))))

(defn add [n-snafu m-snafu]
  (loop [n-remaining (reverse n-snafu)
         m-remaining (reverse m-snafu)
         carry-over \0
         sum []]
    (if (and (empty? n-remaining) (empty? m-remaining) (= \0 carry-over))
      (reverse sum)
      (let [[digit-sum digit-carry] (add-digits (first n-remaining) (first m-remaining))
            [digit-carry-sum digit-carry-carry] (add-digits digit-sum carry-over)]
        (recur
         (rest n-remaining)
         (rest m-remaining)
         (first (add-digits digit-carry digit-carry-carry))
         (concat sum [digit-carry-sum]))))))

(println (->> snafus (reduce add) str/join))
