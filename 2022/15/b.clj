(ns aoc.2022.15.b)

(require '[clojure.math.combinatorics :as combo])

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

(def max-coordinate  (if (= input sample-input) 20 4000000))

(defn parse-sensor-readings [readings]
  (->> readings
       (re-seq #"Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)")
       (map (fn [[_ sensor-x sensor-y beacon-x beacon-y]]
              [[(Integer/parseInt sensor-x) (Integer/parseInt sensor-y)]
               [(Integer/parseInt beacon-x) (Integer/parseInt beacon-y)]]))))

(defn manhatan-distance [[x-first y-first] [x-second y-second]]
  (+
   (abs (- x-first x-second))
   (abs (- y-first y-second))))

(def monitored-areas
  (->> input
       parse-sensor-readings
       (map (fn [[sensor beacon]] [sensor (manhatan-distance sensor beacon)]))))

(defn inside-area? [[[x0 y0] radius] [x y]]
  (<= (manhatan-distance [x0 y0] [x y]) radius))

(defn line-segments-outside [[[x0 y0] radius]]
  (let [extended-radius (inc radius)
        min-x (- x0 extended-radius)
        max-x (+ x0 extended-radius)
        min-y (- y0 extended-radius)
        max-y (+ y0 extended-radius)]
    [[[min-x y0] [x0 min-y]]
     [[x0 min-y] [max-x y0]]
     [[max-x y0] [x0 max-y]]
     [[x0 max-y] [min-x y0]]]))

(def sensor-line-segments (map line-segments-outside monitored-areas))
(def boundary-line-segments [[[0 0] [0 max-coordinate]]
                             [[0 0] [max-coordinate 0]]
                             [[0 max-coordinate] [max-coordinate max-coordinate]]
                             [[max-coordinate 0] [max-coordinate max-coordinate]]])

(def all-line-segments-4s (cons boundary-line-segments sensor-line-segments))

;; https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection#Given_two_points_on_each_line_segment

;; let s1 := segment((x1, y1), (x2, y2))
;; let s2 := segment((x3, y3), (x4, y4))

;; d := ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4))
;; t := ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / d
;; u := ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / d

;; line(s1) ≡ line(s2) iff d = 0
;; s1 ⋂ s2 iff t ∈ [0, 1] ∧ u ∈ [0, 1]

;; (px1, py1) := (x1 + t * (x2 - x1), y1 + t * (y2 - y1))
;; (px2, py2) := (x3 + u * (x4 - x3), y3 + u * (y4 - y3))
;; (px1, py1) ≡ (px2, py2) ≡ s1 ⋂ s2

(defn d [[[x1 y1] [x2 y2]] [[x3 y3] [x4 y4]]]
  (-
   (* (- x1 x2) (- y3 y4))
   (* (- y1 y2) (- x3 x4))))

(defn t [[[x1 y1] [x2 y2] :as l1] [[x3 y3] [x4 y4] :as l2]]
  (let [d (d l1 l2)]
    (if (zero? d) ##Inf
        (/
         (-
          (* (- x1 x3) (- y3 y4))
          (* (- y1 y3) (- x3 x4)))
         (double d)))))

(defn u [[[x1 y1] [x2 y2] :as l1] [[x3 y3] [x4 y4] :as l2]]
  (let [d (d l1 l2)]
    (if (zero? d) ##-Inf
        (/
         (-
          (* (- x1 x3) (- y1 y2))
          (* (- y1 y3) (- x1 x2)))
         (double d)))))

(defn intersection-point [[[x1 y1] [x2 y2]] t]
  [(+ x1 (* t (- x2 x1)))
   (+ y1 (* t (- y2 y1)))])

(defn intersect? [t u]
  (and
   (<= 0 t 1)
   (<= 0 u 1)))

(defn all-intersections [line-segments-4-a line-segments-4-b]
  (for [a line-segments-4-a
        b line-segments-4-b
        :let [t (t a b)
              u (u a b)]]
    (when (intersect? t u) (intersection-point a t))))

(defn intersections [[line-segments-4-a line-segments-4-b]]
  (let [all-intersections (all-intersections line-segments-4-a line-segments-4-b)]
    (->> all-intersections
         (remove nil?)
         distinct)))

(defn inside-viewport? [[x y]]
  (<= 0 (min x y) (max x y) max-coordinate))

(defn inside-any-monitored-area? [areas [x y :as point]]
  (cond
    (empty? areas) false
    (inside-area? (first areas) point) true
    :else (recur (rest areas) point)))

(def missing-beacon
  (->> (combo/combinations all-line-segments-4s 2)
       (mapcat intersections)
       distinct
       (filter inside-viewport?)
       (remove #(inside-any-monitored-area? monitored-areas %))
       first
       (map long)))

(pr
 (let [[x y] missing-beacon]
   (+ (* x 4000000) y)))
