(ns aoc.2022.5.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
  ")

(def input (aoc.2022/read-input sample-input))

(def unparsed-initial-setup (-> input (str/split #"\n\n") first str/split-lines (#(remove empty? %))))
(def unparsed-instructions (-> input str/trim (str/split #"\n\n") last str/split-lines))

(def initial-setup (map
                    (fn [line] (map (fn [crate] (when-not (str/blank? crate) crate)) (re-seq #" {4}|[A-Z]" line)))
                    (butlast unparsed-initial-setup)))

(defn parse-instruction [instruction]
  (->> instruction (re-seq #"move (\d+) from (\d+) to (\d+)") first rest (map #(Integer/parseInt %))))

(def instructions (map parse-instruction unparsed-instructions))

(def setup-x (->> initial-setup (apply mapv vector) (map #(remove nil? %))))

(defn move [setup from to count]
  (let [crate (->> from dec (nth setup) (take count))
        new-to (->> to dec (nth setup) (#(into % (reverse crate))))
        new-from (->> from dec (nth setup) (drop count))]
    (assoc-in
     (assoc-in (vec setup) [(dec to)] new-to)
     [(dec from)] new-from)))

(defn run [setup instructions]
  (if (empty? instructions) setup
      (let [[count from to] (first instructions)
            remaining-instructions (rest instructions)]
        (recur (move setup from to count) remaining-instructions))))

(def final-setup (run setup-x instructions))

(pr (->> final-setup (map first) str/join))
