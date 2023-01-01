(ns aoc.2022.23.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
....#..
..###.#
#...#.#
.#...##
#.###..
##.#.##
.#..#..
  ")

(def input (aoc.2022/read-input sample-input))

(def elves (->> input
                str/split-lines
                (map str/trim)
                (map seq)
                (map (fn [line] (keep-indexed #(when-not (#{\.} %2) %1) line)))
                (keep-indexed #(identity [%1 %2]))
                (mapcat (fn [[line-index line]]
                          (map (fn [column-index] [line-index column-index]) line)))
                set))

(def directions (cycle [:n :s :w :e]))

(defn neighbours? [[y x :as elf] elves direction]
  (->> (case direction
         :n [(elves [(dec y) (dec x)]) (elves [(dec y) x]) (elves [(dec y) (inc x)])]
         :s [(elves [(inc y) (dec x)]) (elves [(inc y) x]) (elves [(inc y) (inc x)])]
         :w [(elves [(dec y) (dec x)]) (elves [y (dec x)]) (elves [(inc y) (dec x)])]
         :e [(elves [(dec y) (inc x)]) (elves [y (inc x)]) (elves [(inc y) (inc x)])])
       (some identity)))

(defn move-in [[y x :as elf] direction]
  (case direction
    :n [(dec y) x]
    :s [(inc y) x]
    :w [y (dec x)]
    :e [y (inc x)]))

(defn proposed-destination [elf elves directions]
  (let [unoccupied-directions (->> directions
                                   (take 4)
                                   (remove #(neighbours? elf elves %)))]
    (when-not (= 4 (count unoccupied-directions))
      (->> unoccupied-directions (map #(move-in elf %)) first))))

(defn spread-out
  ([elves directions] (spread-out elves directions 10))
  ([elves directions timer]
   (if (zero? timer) elves
       (recur
        (->> elves
             (map #(list % (proposed-destination % elves directions)))
             (group-by last)
             (mapcat (fn [[destination elves-with-destination]]
                       (if (or (nil? destination) (< 1 (count elves-with-destination)))
                         (map first elves-with-destination)
                         [destination])))
             set)
        (rest directions)
        (dec timer)))))

(defn empty-spaces [elves]
  (let [min-x (->> elves (map last) (apply min))
        max-x (->> elves (map last) (apply max))
        min-y (->> elves (map first) (apply min))
        max-y (->> elves (map first) (apply max))
        x-side (inc (- max-x min-x))
        y-side (inc (- max-y min-y))]
    (- (* x-side y-side) (count elves))))

(pr (empty-spaces (spread-out elves directions)))
