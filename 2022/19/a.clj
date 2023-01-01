(ns aoc.2022.19.a)

(load-file "read_input.clj")

(def sample-input
  "
Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
  ")

(def input (aoc.2022/read-input sample-input))

(defn parse-blueprints [blueprints]
  (->> blueprints
       (re-seq #"Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.")
       (map (fn [[_ number ore->ore ore->clay ore->obsidian clay->obsidian ore->geode obsidian->geode]]
              {:number (Integer/parseInt number)
               :none {:ore 0, :clay 0, :obsidian 0}
               :ore {:ore (Integer/parseInt ore->ore), :clay 0, :obsidian 0}
               :clay {:ore (Integer/parseInt ore->clay), :clay 0, :obsidian 0}
               :obsidian {:ore (Integer/parseInt ore->obsidian), :clay (Integer/parseInt clay->obsidian), :obsidian 0}
               :geode {:ore (Integer/parseInt ore->geode), :clay 0, :obsidian (Integer/parseInt obsidian->geode)}}))))

(def blueprints (parse-blueprints input))

(defn income [robots resources]
  (when resources (merge-with + robots resources)))

(defn max-extraction-capacity-reached? [kind blueprint robots]
  (case kind
    :obsidian (>= (:obsidian robots) (get-in blueprint [:geode :obsidian]))
    :clay (>= (:clay robots) (get-in blueprint [:obsidian :clay]))
    :ore (>= (:ore robots) (max
                            (get-in blueprint [:clay :ore])
                            (get-in blueprint [:obsidian :ore])
                            (get-in blueprint [:geode :ore])))
    false))

(defn construct-robot [kind blueprint resources timer robots]
  (cond
    (max-extraction-capacity-reached? kind blueprint robots) nil
    (and (= kind :geode) (>= timer 23)) nil
    ;; Magic numbers (trial and error) pruning.
    ;; Might not work for some inputs.
    ;; If you don't get the correct answer, try to increase the numbers below by a bit.
    ;;   It would take longer, but might find a more optimal build.
    (and (= kind :obsidian) (>= timer 21)) nil
    (and (= kind :clay) (>= timer 17)) nil
    (and (= kind :ore) (>= timer 13)) nil
    :else (let [remaining-resources (merge-with - resources (kind blueprint))
                resources-available (->> remaining-resources vals (some neg?) not)]
            (when resources-available remaining-resources))))

(defn add-robot [kind robots]
  (if (= :none kind) robots (update robots kind inc)))

(def run
  (memoize
   (fn
     ([blueprint] (run
                   blueprint
                   {:ore 0, :clay 0, :obsidian 0, :geode 0}
                   {:ore 1, :clay 0, :obsidian 0, :geode 0}
                   0))
     ([blueprint resources robots timer]
      (if (= timer 24) resources
          (->>
           (for [robot-kind [:none :ore :clay :obsidian :geode]]
             (let [resources-after-construction (construct-robot robot-kind blueprint resources timer robots)
                   resources-after-income (income robots resources-after-construction)
                   robots-after-construction (add-robot robot-kind robots)]
               (when resources-after-construction
                 (run blueprint resources-after-income robots-after-construction (inc timer)))))
           (remove nil?)
           (sort-by :geode)
           last))))))

(defn quality-level [blueprint]
  (* (:number blueprint) (:geode (run blueprint))))

;; 46.72s user 1.82s system 184% cpu 26.326 total
(pr (->> blueprints
         (map quality-level)
         (reduce +)))
