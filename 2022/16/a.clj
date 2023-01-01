(ns aoc.2022.16.a)

(require '[clojure.string :as str])

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

(def release-pressure
  (memoize
   (fn
     ([] (release-pressure "AA" #{} 30))
     ([location opened-valves timer]
      (if (zero? timer) 0
          (let [dont-try-open-valve? (or
                                      (opened-valves location)
                                      (zero? (get-in cave-system [location :rate])))]
            (+
             (pressure-from opened-valves)
             (reduce
              max
              (if dont-try-open-valve? 0
                  (release-pressure location (conj opened-valves location) (dec timer)))
              (for [next-valve (get-in cave-system [location :tunnels])]
                (release-pressure next-valve opened-valves (dec timer)))))))))))

(pr (release-pressure))
