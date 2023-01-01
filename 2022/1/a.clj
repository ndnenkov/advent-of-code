(ns aoc.2022.1.a)

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

(def most-calories (->> elves
                        (map #(reduce + %))
                        (apply max)))

(pr most-calories)
