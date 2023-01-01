(ns aoc.2022.15.a)

(load-file "read_input.clj")

(def sample-input
  "
Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3
  ")

(def input (aoc.2022/read-input sample-input))

(def goal-row (if (= input sample-input) 10 2000000))

(defn parse-sensor-readings [readings]
  (->> readings
       (re-seq #"Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)")
       (map (fn [[_ sensor-x sensor-y beacon-x beacon-y]]
              [[(Integer/parseInt sensor-x) (Integer/parseInt sensor-y)]
               [(Integer/parseInt beacon-x) (Integer/parseInt beacon-y)]]))))

(def sensor-readings (parse-sensor-readings input))

(defn range-union [[start-first end-first :as first-range] [start-second end-second :as second-range]]
  (cond
    (empty? first-range) [second-range]
    (empty? second-range) [first-range]
    (<= start-first start-second end-second end-first) [first-range]
    (<= start-second start-first end-first end-second) [second-range]
    (<= start-first end-second end-first) [[start-second end-first]]
    (<= start-second end-first end-second) [[start-first end-second]]
    :else [first-range second-range]))

(defn manhatan-distance [[x-first y-first] [x-second y-second]]
  (+
   (abs (- x-first x-second))
   (abs (- y-first y-second))))

(defn at-manhatan-distance [[from-x from-y] distance y]
  (let [distance-y (abs (- from-y y))
        leeway-x (- distance distance-y)]
    (if (neg? leeway-x) [] [(- from-x leeway-x) (+ from-x leeway-x)])))

(def ranges-at-distance (->> sensor-readings
                             (map (fn [[sensor beacon]] [sensor (manhatan-distance sensor beacon)]))
                             (map (fn [[sensor distance]] (at-manhatan-distance sensor distance goal-row)))
                             (remove empty?)
                             (sort-by first)))

(def merged-left (sort-by last (reduce #(into (rest %1) (range-union (first %1) %2)) [(first ranges-at-distance)] ranges-at-distance)))
(def merged-right (reduce #(into (drop-last %1) (range-union (last %1) %2)) [(first ranges-at-distance)] ranges-at-distance))

(def beacons-at-goal-row (->> sensor-readings
                              (map last)
                              (map #(when (= (last %) goal-row) (first %)))
                              (remove nil?)))

(defn range-size [[from to]] (inc (- to from)))

(defn split-range-at [[from to] at]
  (cond
    (= from to at) []
    (= from at) [[(inc from) to]]
    (= to at) [[from (dec to)]]
    (< from at to) [[from at] [at to]]
    :else [[from to]]))

(defn split-all [ranges points]
  (if (empty? points) ranges (recur (mapcat #(split-range-at % (first points)) ranges) (rest points))))

(pr (->> (split-all merged-right beacons-at-goal-row) (map range-size) (reduce +)))
