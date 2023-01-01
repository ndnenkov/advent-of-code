(ns aoc.2022.21.a)

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

(def leafs (->> input
                (re-seq #"([a-z]{4}): (\d+)")
                (map (fn [[_ name number]] {name (Integer/parseInt number)}))
                (into {})))

(def nodes (->> input
                (re-seq #"([a-z]{4}): ([a-z]{4}) ([-+*/]) ([a-z]{4})")
                (map (fn [[_ name first-name operation second-name]]
                       {:name name,
                        :first-name first-name,
                        :second-name second-name,
                        :operation (load-string operation)}))))

(defn shoutout [nodes leafs]
  (if (leafs "root") (leafs "root")
      (let [node (->> nodes (filter #(and (leafs (:first-name %)) (leafs (:second-name %)))) first)
            first-operand (leafs (:first-name node))
            second-operand (leafs (:second-name node))
            operation (:operation node)]
        (recur
         (remove #{node} nodes)
         (assoc leafs (:name node) (operation first-operand second-operand))))))

(pr (shoutout nodes leafs))
