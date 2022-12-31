(ns aoc.2022.10.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop
  ")

(def input (aoc.2022/read-input sample-input))

(def instructions (->> input
                       str/trim
                       str/split-lines
                       (map str/trim)
                       (map #(str/split % #"\s"))
                       (map (fn [[operation value]] (list operation (when value (Integer/parseInt value)))))))

(defn run
  ([instructions] (run instructions 1 []))
  ([instructions x xes]
   (if (empty? instructions) xes
       (let [remaining-instructions (rest instructions)
             instruction (first instructions)
             operation (first instruction)
             value (last instruction)]
         (case operation
           "noop" (recur remaining-instructions x (conj xes x))
           "addx" (let [new-x (+ x value)]
                    (recur remaining-instructions new-x (into xes [x x]))))))))

(def x-values (run instructions))
(defn print-image [pixels] (->> pixels (partition 40 40) (map str/join) (str/join "\n")))
(def image (map-indexed #(if (<= (dec %2) (mod %1 40) (inc %2)) "■" " ") x-values))

(println (print-image image))
