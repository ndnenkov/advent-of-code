(ns aoc.2022.22.b)

(require '[clojure.string :as str])
(require '[clojure.math.numeric-tower :as num])

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

(def cube (->> (str/split input #"\n\n")
               first
               str/split-lines
               (remove empty?)
               (map seq)
               (map (fn [line] (keep-indexed #(when-not (#{\space} %2) [(inc %1) %2]) line)))
               (keep-indexed #(identity [(inc %1) %2]))
               (mapcat (fn [[line-index line]]
                         (map (fn [[column-index value]] {[line-index column-index] value}) line)))
               (into {})))

(def instructions (->> input
                       (re-seq #"(\d+)|([RL])")
                       (map (fn [[_ distance direction]]
                              (if distance (Integer/parseInt distance) direction)))))

(def side-size (-> cube count (/ 6) num/sqrt))

(defn fit-side [p]
  (let [n side-size]
    (if (zero? (mod p n)) n (mod p n))))

(defn complement-side [p]
  (- (inc side-size) (fit-side p)))

(def sample-side-bounds
  (let [n side-size
        n2 (* 2 n)
        n3 (* 3 n)
        n4 (* 4 n)
        n+1 (inc n)
        n2+1 (inc n2)
        n3+1 (inc n3)]
    {:a [[1    n]  [n2+1 n3]],
     :b [[n+1  n2] [1    n]],
     :c [[n+1  n2] [n+1  n2]],
     :d [[n+1  n2] [n2+1 n3]],
     :e [[n2+1 n3] [n2+1 n3]],
     :f [[n2+1 n3] [n3+1 n4]]}))

(def sample-rotations
  {[:a   0] [:f 2],
   [:a 180] [:c 3],
   [:a 270] [:b 2],
   [:b  90] [:e 2],
   [:b 180] [:f 1],
   [:b 270] [:a 2],
   [:c  90] [:e 3],
   [:c 270] [:a 1],
   [:d   0] [:f 1],
   [:e  90] [:b 2],
   [:e 180] [:c 1],
   [:f   0] [:a 2],
   [:f  90] [:b 3],
   [:f 270] [:d 3]})

(def real-side-bounds
  (let [n side-size
        n2 (* 2 n)
        n3 (* 3 n)
        n4 (* 4 n)
        n+1 (inc n)
        n2+1 (inc n2)
        n3+1 (inc n3)]
    {:a [[1 n] [n+1 n2]]
     :b [[1 n] [n2+1 n3]]
     :c [[n+1 n2] [n+1 n2]]
     :d [[n2+1 n3] [1 n]]
     :e [[n2+1 n3] [n+1 n2]]
     :f [[n3+1 n4] [1 n]]}))

(def real-rotations
  {[:a 180] [:d 2]
   [:a 270] [:f 1]
   [:b   0] [:e 2]
   [:b  90] [:c 1]
   [:b 270] [:f 0]
   [:c   0] [:b 3]
   [:c 180] [:d 3]
   [:d 180] [:a 2]
   [:d 270] [:c 1]
   [:e   0] [:b 2]
   [:e  90] [:f 1]
   [:f   0] [:e 3]
   [:f  90] [:b 0]
   [:f 180] [:a 3]})

(def side-bounds (if (= input sample-input) sample-side-bounds real-side-bounds))
(def rotations (if (= input sample-input) sample-rotations real-rotations))

(defn spin [direction instruction]
  (mod (+ direction ({"L" -90, "R" 90} instruction)) 360))

(defn ->side-coordinates [[y x] side rotations]
  (if (zero? rotations)
    (let [[[min-y _] [min-x _]] (side-bounds side)]
      [(+ y min-y -1) (+ x min-x -1)])
    (recur [x y] side (dec rotations))))

(defn current-side [y x]
  (some
   (fn [[side [[min-y max-y] [min-x max-x]]]]
     (when (and (<= min-y y max-y) (<= min-x x max-x)) side))
   side-bounds))

(defn wrap-around [[y x :as position] direction]
  (let [[to-side rotation-count] (rotations [(current-side y x) direction])
        fit-y? (#{[0 3] [90 1] [90 2] [180 3] [270 1] [270 2]} [direction rotation-count])
        fit-x? (#{[0 2] [0 3] [90 0] [90 1] [180 2] [180 3] [270 0] [270 1]} [direction rotation-count])
        transformed-coordinates [(if fit-y? (fit-side y) (complement-side y))
                                 (if fit-x? (fit-side x) (complement-side x))]]
    [(->side-coordinates transformed-coordinates to-side rotation-count)
     ((apply comp (repeat rotation-count #(spin % "R"))) direction)]))

(defn next-state [cube [y x :as position] direction]
  (let [next-position (case direction
                        0 [y (inc x)]
                        90 [(inc y) x]
                        180 [y (dec x)]
                        270 [(dec y) x])]
    (case (cube next-position)
      \. [next-position direction]
      \# [position direction]
      (let [[next-position next-direction] (wrap-around position direction)]
        (if (= \. (cube next-position))
          [next-position next-direction]
          [position direction])))))

(defn move
  ([cube instructions] (move cube (-> cube keys sort first) 0 instructions))
  ([cube position direction instructions]
   (if (empty? instructions) [position direction]
       (let [instruction (first instructions) other-instructions (rest instructions)]
         (if (#{"L" "R"} instruction)
           (recur cube position (spin direction instruction) other-instructions)
           (let [[next-position next-direction] (next-state cube position direction)]
             (if (= instruction 1)
               (recur cube next-position next-direction other-instructions)
               (recur cube next-position next-direction (concat [(dec instruction)] other-instructions)))))))))

(let [[[y x] direction] (move cube instructions)]
  (pr
   (+
    (* 1000 y)
    (* 4 x)
    (/ direction 90))))
