(ns aoc.2022.17.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
  ")

(def input (aoc.2022/read-input sample-input))

(def winds (-> input str/trim cycle))

(def rocks
  (cycle
   (map
    (fn [rock]
      (map
       #(-> %
            (str/replace #"\." "0")
            (str/replace #"#" "1")
            (Integer/parseInt 2))
       rock))
    [["......."
      "......."
      "......."
      "..####."] ;; ####

     ["......."
      "...#..."  ;; .#.
      "..###.."  ;; ###
      "...#..."] ;; .#.

     ["......."
      "....#.."  ;; ..#
      "....#.."  ;; ..#
      "..###.."] ;; ###

     ["..#...."  ;; #
      "..#...."  ;; #
      "..#...."  ;; #
      "..#...."] ;; #

     ["......."
      "......."
      "..##..."   ;; ##
      "..##..."]] ;; ##
    )))

(defn trim-top [mound]
  (drop-while zero? mound))

(defn colide? [rock space]
  (some
   #(pos? (reduce bit-and %))
   (map vector rock space)))

(defn blow [rock wind mound fell]
  (cond
    (and (= wind \<) (some #(>= % 64) rock)) rock
    (and (= wind \>) (some odd? rock)) rock
    :else (let [operation ({\< bit-shift-left, \> bit-shift-right} wind)
                blown-rock (map #(operation % 1) rock)
                space (take 4 (drop fell mound))]
            (if (colide? blown-rock space) rock blown-rock))))

(defn fall [rock mound fell]
  (let [space (take 4 (drop (inc fell) mound))]
    (when-not (colide? rock space) (inc fell))))

(defn solidify [rock mound fell]
  (let [before (take fell mound)
        after (drop (+ fell 4) mound)
        at (->> mound (drop fell) (take 4))
        merged (->> rock (map vector at) (map #(reduce bit-or %)))]
    (->> (concat before merged after)
         trim-top
         (concat '(0 0 0 0 0 0 0)))))

(defn avalanche
  ([rock-generator wind-generator]
   (avalanche '(0 0 0 0 0 0 0 127) (first rock-generator) (first wind-generator) 0 (rest rock-generator) (rest wind-generator) 0))
  ([mound rock wind fell-to rock-generator wind-generator solidified-rocks]
   (if (= solidified-rocks 2022) mound
       (let [blown-rock (blow rock wind mound fell-to)
             fell (fall blown-rock mound fell-to)]
         (if fell
           (recur
            mound
            blown-rock
            (first wind-generator)
            fell
            rock-generator
            (rest wind-generator)
            solidified-rocks)
           (recur
            (solidify blown-rock mound fell-to)
            (first rock-generator)
            (first wind-generator)
            0
            (rest rock-generator)
            (rest wind-generator)
            (inc solidified-rocks)))))))

(pr
 (-> (avalanche rocks winds)
     trim-top
     count
     dec))
