(ns aoc.2022.11.a)

(require '[clojure.string :as str])
(require '[clojure.math.numeric-tower :as num])

(load-file "read_input.clj")

(def sample-input
  "
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
  ")

(def input (aoc.2022/read-input sample-input))

(defn parse-monkey [description]
  (let [lines (->> description str/split-lines (map str/trim))],
    {:number (->> lines first (re-matches #"Monkey (\d+):") last Integer/parseInt),
     :items (->> (nth lines 1) (re-seq #"\d+") (map #(Integer/parseInt %))),
     :operation (let [[other old operation second-operand] (re-matches #"Operation: new = (old) ([-+*]) (\d+|old)" (nth lines 2))
                      parsed-operation (load-string operation)]
                  (if (= second-operand "old") #(parsed-operation %1 %1) #(parsed-operation %1 (Integer/parseInt second-operand))))
     :test (->> (nth lines 3) (re-matches #"Test: divisible by (\d+)") last Integer/parseInt),
     :dispatch-true (->> (nth lines 4) (re-matches #"If true: throw to monkey (\d+)") last Integer/parseInt),
     :dispatch-false (->> (nth lines 5) (re-matches #"If false: throw to monkey (\d+)") last Integer/parseInt),
     :inspections 0}))

(def monkeys (->> input str/trim (#(str/split % #"\n\n")) (map parse-monkey)))

(defn throwoff [monkeys turn]
  (if (empty? ((nth monkeys turn) :items)) monkeys
      (let [player (nth monkeys turn)
            item (first (player :items))
            remaining-items (rest (player :items))
            after-operation ((player :operation) item)
            after-boredom (-> after-operation (/ 3) num/floor)
            test-passed (-> after-boredom (mod (player :test)) zero?)
            pass-to (nth monkeys (player (if test-passed :dispatch-true :dispatch-false)))
            updated-player (-> player (update :items (fn [_] remaining-items)) (update :inspections inc))
            updated-pass-to (update pass-to :items (fn [items] (conj (vec items) after-boredom)))]
        (recur
         (-> monkeys
             vec
             (update turn (fn [_] updated-player))
             (update (pass-to :number) (fn [_] updated-pass-to)))
         turn))))

(defn play
  ([monkeys] (play monkeys 1 0))
  ([monkeys round turn]
   (let [last-turn (->> monkeys count dec (= turn))
         next-turn (if last-turn 0 (inc turn))
         next-round (if last-turn (inc round) round)
         next-monkeys (throwoff monkeys turn)]
     (if (= 21 next-round) next-monkeys
         (recur next-monkeys next-round next-turn)))))

(def monkey-business (->> monkeys play (map #(% :inspections)) sort (take-last 2) (apply *)))
(pr monkey-business)
