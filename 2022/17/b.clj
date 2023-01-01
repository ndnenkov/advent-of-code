(ns aoc.2022.17.b)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
  ")

(def input (aoc.2022/read-input sample-input))

(defn pretty [row]
  (-> row (Integer/toString 2) (->> (format "%7s")) (str/replace #"[0 ]" ".") (str/replace #"1" "#")))

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

(defn detect-tripple-cycle [coll]
  (seq
   (for [size (range 50 (max 101 (inc (/ (count coll) 3))))
         :let [x (take size coll)
               y (take size (drop size coll))
               z (take size (drop size (drop size coll)))]
         :when (= x y z)]
     size)))

(defn avalanche
  ([rock-generator wind-generator]
   (avalanche '(0 0 0 0 0 0 0 127) (first rock-generator) (first wind-generator) 0 (rest rock-generator) (rest wind-generator) 0))
  ([mound rock wind fell-to rock-generator wind-generator solidified-rocks]
  ;;  (if (= solidified-rocks 50000) mound
   (if (= solidified-rocks (+ 139 3471)) mound
       (let [blown-rock (blow rock wind mound fell-to)
             fell (fall blown-rock mound fell-to)]
         (if fell
          ;;  (do
            ;;  (let [cycle-size (detect-tripple-cycle (trim-top mound))]
            ;;    (when cycle-size (println [cycle-size solidified-rocks])))

            ;;  (when (= solidified-rocks 5344) (println (count mound)))
            ;;  (when (= (count mound) 209) (println solidified-rocks))

           (recur
            mound
            blown-rock
            (first wind-generator)
            fell
            rock-generator
            (rest wind-generator)
            solidified-rocks)
              ;; )
           (recur
            (solidify blown-rock mound fell-to)
            (first rock-generator)
            (first wind-generator)
            0
            (rest rock-generator)
            (rest wind-generator)
            (inc solidified-rocks)))))))

;; [(2673 5346) 10936]
;; [(2673) 5344]
;; 8228 - 3*2673 = 209
;; 209 mound at 139 solidified rocks
;; 5344 - 139 = 5205 solidified rocks cycle x3, height = 3x2673 = 8019
;; (1000000000000 - 139) / 5205 = 192122958 cycles x3
;; 192122958 * 3*2673 = 1540634000202 height
;; (1000000000000 - 139) % 5205 = 3471 remaining rocks to solidify
(pr
 (-> (avalanche rocks winds)
     trim-top
     count
     dec
     (+ 1540634000202)))
