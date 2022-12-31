(ns aoc.2022)

(require '[clojure.string :as str])

(defn read-input [sample-input]
  (str/trim
   (if *command-line-args*
     sample-input
     (let [task-number (-> *file* (str/split #"/") butlast last)
           input-path (str/replace *file* #"(?<=/)\d{1,2}/[ab]\.clj$" (str "inputs/" task-number ".txt"))]
       (slurp input-path)))))
