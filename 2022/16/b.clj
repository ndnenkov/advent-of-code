(ns aoc.2022.16.b)

(require '[clojure.string :as str])
(require '[clojure.set :as set])

(load-file "read_input.clj")

(def sample-input
  "
Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II
  ")

(def input (aoc.2022/read-input sample-input))

(defn parse-scan-readings [readings]
  (->> readings
       (re-seq #"Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ((?:[A-Z]{2},? ?)+)")
       (map (fn [[_ valve flow-rate tunnels-to]]
              {valve {:rate (Integer/parseInt flow-rate), :tunnels (str/split tunnels-to #", ")}}))
       (into {})))

(def cave-system (parse-scan-readings input))

(def pressure-from
  (memoize
   (fn [valves]
     (->> cave-system
          (filter #(valves (key %)))
          (map last)
          (map :rate)
          (reduce +)))))

(defn bfs
  ([from to] (bfs to [from] #{} 0))
  ([to to-visit visited step]
   (if (some #{to} to-visit) step
       (recur
        to
        (->> to-visit (mapcat #(get-in cave-system [% :tunnels])) distinct (remove visited))
        (set/union visited (set to-visit))
        (inc step)))))

(def shortest-path
  (memoize (fn [from to] (bfs from to))))

(def valves-worth-opening (->>
                           cave-system
                           seq
                           (remove #(zero? (get-in % [1 :rate])))
                           (map first)))

(def release-pressure
  (memoize
   (fn
     ([] (release-pressure "AA" 26 "AA" 26 #{}))
     ([my-location my-timer elephant-location elephant-timer opened]
      (apply max
             (concat [0]
                     (for [goal (remove opened valves-worth-opening)
                           :let [time-to-open (inc (shortest-path my-location goal))
                                 time-left (- my-timer time-to-open)
                                 gain (* time-left (get-in cave-system [goal :rate]))]
                           :when (pos? gain)]
                       (+ gain (release-pressure goal time-left elephant-location elephant-timer (conj opened goal))))

                     (for [goal (remove opened valves-worth-opening)
                           :let [time-to-open (inc (shortest-path elephant-location goal))
                                 time-left (- elephant-timer time-to-open)
                                 gain (* time-left (get-in cave-system [goal :rate]))]
                           :when (pos? gain)]
                       (+ gain (release-pressure my-location my-timer goal time-left (conj opened goal))))))))))

;; 269.94s user 3.47s system 167% cpu 2:43.06 total
(pr (release-pressure))
