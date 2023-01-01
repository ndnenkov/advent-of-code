(ns aoc.2022.22.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
  ")

(def input (aoc.2022/read-input sample-input))

(def elven-map (->> (str/split input #"\n\n")
                    first
                    str/split-lines
                    (remove empty?)
                    (map seq)
                    (map (fn [line] (keep-indexed #(when-not (#{\space} %2) [%1 %2]) line)))
                    (keep-indexed #(identity [%1 %2]))
                    (mapcat (fn [[line-index line]]
                              (map (fn [[column-index value]] {[line-index column-index] value}) line)))
                    (into {})))

(def instructions (->> input
                       (re-seq #"(\d+)|([RL])")
                       (map (fn [[_ distance direction]]
                              (if distance (Integer/parseInt distance) direction)))))

(defn opposite-position [elven-map coordinate-kind value extremum-kind]
  (->> elven-map
       keys
       (filter (fn [[y x]] (#{value} ({:y y, :x x} coordinate-kind))))
       sort
       (({:min first, :max last} extremum-kind))))

(defn next-position [elven-map [y x :as position] direction]
  (let [next-position (case direction
                        0 [y (inc x)]
                        90 [(inc y) x]
                        180 [y (dec x)]
                        270 [(dec y) x])]
    (case (elven-map next-position)
      \. next-position
      \# position
      (case direction
        0 (opposite-position elven-map :y y :min)
        90 (opposite-position elven-map :x x :min)
        180 (opposite-position elven-map :y y :max)
        270 (opposite-position elven-map :x x :max)))))

(defn spin [direction instruction]
  (mod (+ direction ({"L" -90, "R" 90} instruction)) 360))

(defn move
  ([elven-map instructions] (move elven-map (-> elven-map keys sort first) 0 instructions))
  ([elven-map position direction instructions]
   (if (empty? instructions) [position direction]
       (let [instruction (first instructions) other-instructions (rest instructions)]
         (if (#{"L" "R"} instruction)
           (recur elven-map position (spin direction instruction) other-instructions)
           (let [next-position (next-position elven-map position direction)]
             (if (= 1 instruction)
               (recur elven-map next-position direction other-instructions)
               (recur elven-map next-position direction (concat [(dec instruction)] other-instructions)))))))))

(let [[[y x] direction] (move elven-map instructions)]
  (pr
   (+
    (* 1000 (inc y))
    (* 4 (inc x))
    (/ direction 90))))
