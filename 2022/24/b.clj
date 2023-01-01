(ns aoc.2022.24.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#
  ")

(def input (aoc.2022/read-input sample-input))

(def field (->> input
                str/trim
                str/split-lines
                (mapv str/trim)
                (mapv seq)
                (mapv (fn [line] (mapv {\# \#, \. #{}, \> #{\>}, \< #{\<}, \v #{\v}, \^ #{\^}} line)))))

(defn blizzard-destination [y x blizzard field]
  (let [height (count field) width (count (first field))]
    (case blizzard
      \> (if (= \# (get-in field [y (inc x)])) [y 1]            [y (inc x)])
      \< (if (= \# (get-in field [y (dec x)])) [y (- width 2)]  [y (dec x)])
      \v (if (= \# (get-in field [(inc y) x])) [1 x]            [(inc y) x])
      \^ (if (= \# (get-in field [(dec y) x])) [(- height 2) x] [(dec y) x]))))

(defn move-blizzards [y x field blizzards]
  (let [blizzard (first blizzards)]
    (if-not blizzard
      field
      (recur
       y
       x
       (update-in field (blizzard-destination y x blizzard field) #(conj % blizzard))
       (disj blizzards blizzard)))))

(defn remove-blizzards [field]
  (mapv
   (fn [row] (mapv #(or (#{\#} %) #{}) row))
   field))

(defn blizzard-turn [field]
  (loop [y 1
         x 1
         updated-field (remove-blizzards field)]
    (let [blizzards (get-in field [y x])]
      (cond
        (nil? blizzards) updated-field
        (= \# blizzards) (recur (inc y) 1 updated-field)
        (= #{} blizzards) (recur y (inc x) updated-field)
        :else (recur y (inc x) (move-blizzards y x updated-field blizzards))))))

(defn cycle-field
  ([field] (cycle-field field []))
  ([field fields]
   (let [next-field (blizzard-turn field)
         fields (concat fields [field])]
     (if (= next-field (first fields))
       (cycle fields)
       (recur next-field fields)))))

(defn neighbours [[y x] field]
  (filter
   #(= #{} (get-in field %))
   [[y (inc x)]
    [(inc y) x]
    [(dec y) x]
    [y (dec x)]
    [y x]]))

(defn bfs
  ([fields to-visit goal step]
   (cond
     (empty? to-visit) [fields ##Inf]
     (some #{goal} to-visit) [fields step]
     :else (recur
            (rest fields)
            (distinct (mapcat #(neighbours % (first fields)) to-visit))
            goal
            (inc step)))))

(let [start [0 1]
      end [(dec (count field)) (- (count (first field)) 2)]
      initial-fields (rest (cycle-field field))
      [first-trip-fields first-trip-time] (bfs initial-fields [start] end 0)
      [back-trip-fields back-trip-time] (bfs first-trip-fields [end] start first-trip-time)
      [_ final-time] (bfs back-trip-fields [start] end back-trip-time)]
  (pr final-time))
