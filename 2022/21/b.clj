(ns aoc.2022.21.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32
  ")

(def input (aoc.2022/read-input sample-input))

(def expressions (->> input
                      str/trim
                      str/split-lines
                      (map #(str/split % #": "))
                      (map (fn [[name expression]] {name expression}))
                      (into {})))

(def amended-expressions (-> expressions
                             (assoc "humn" "x")
                             (update "root" #(str/replace % #"[-+*/]" "="))))

(defn replace-all [expressions]
  (if (= 1 (count expressions)) (expressions "root")
      (let [first-name (->> expressions keys (remove #{"root"}) first)
            expression (expressions first-name)
            other (dissoc expressions first-name)]
        (recur (into {} (for [[k v] other] {k (str/replace v first-name (str "(" expression ")"))}))))))

(println "Input into https://www.mathpapa.com/algebra-calculator.html")
(println (replace-all amended-expressions))
