(ns aoc.2022.12.b)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

(load-file "read_input.clj")

(def sample-input
  "
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi
  ")

(def input (aoc.2022/read-input sample-input))

(def heights (-> input (str/replace #"\s" "") char-array vec))
(def width (-> input str/split-lines count (- 2)))
(def height (-> input str/split-lines second count))

(defn numeric-value [letter]
  (case letter
    \S 1
    \E 26
    (-> letter int (- (int \a)) inc)))

(def nodes (map
            (fn [letter] {:letter letter,
                          :elevation (numeric-value letter),
                          :start? (= letter \S),
                          :end? (= letter \E)})
            heights))

(def heightmap (->> nodes (partition height height) (map vec) vec))

(defn neighbours [[x y]]
  (let [x-neighbours (filter #(<= 0 % (dec width)) [(dec x) (inc x)])
        y-neighbours (filter #(<= 0 % (dec height)) [(dec y) (inc y)])]
    (set (into (for [x x-neighbours] [x y]) (for [y y-neighbours] [x y])))))

(defn viable-neighbours [[x y] heightmap visited]
  (let [location-elevation (get-in heightmap [x y :elevation])
        immediate-neighbours (neighbours [x y])
        unvisited-neighbours (set/difference immediate-neighbours visited)]
    (filter #(-> heightmap (get-in (flatten [% :elevation])) (<= (inc location-elevation))) unvisited-neighbours)))

(def viable-starts (filter #(= 1 (:elevation (get-in heightmap %))) (for [x (range width), y (range height)] [x y])))

(defn bfs
  ([heightmap to-visit visited step]
   (cond
     (empty? to-visit) ##Inf
     (->> to-visit (filter #(:end? (get-in heightmap %))) first) step
     :else (bfs
            heightmap
            (->> to-visit (mapcat #(viable-neighbours % heightmap visited)) distinct)
            (->> to-visit set (set/union visited))
            (inc step)))))

(def times (map #(bfs heightmap [%] #{} 0) viable-starts))
(pr (apply min times))
