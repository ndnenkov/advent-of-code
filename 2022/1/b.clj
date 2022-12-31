(ns aoc.2022.1.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
  ")

(def input (aoc.2022/read-input sample-input))

(def elves (-> input
               str/trim
               (str/split #"\n\n")
               (->> (map str/split-lines)
                    (map (fn [elf] (->> elf
                                        (map str/trim)
                                        (map #(Integer/parseInt %))))))))

(def top-3-sum (->> elves
                    (map #(reduce + %))
                    sort
                    (take-last 3)
                    (reduce +)))

(pr top-3-sum)
