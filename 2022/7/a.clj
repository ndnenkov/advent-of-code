(ns aoc.2022.7.a)

(require '[clojure.string :as str])

(load-file "read_input.clj")

(def sample-input
  "
$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
  ")

(def input (aoc.2022/read-input sample-input))

(def output (->> input str/trim str/split-lines (map str/trim)))

(defn resolve-cd [path directory]
  (case directory
    "/" ["/"]
    ".." (pop path)
    (into path (list directory))))

(defn resolve-ls
  ([tree path directory] (resolve-ls tree path {} directory))
  ([tree path value file] (as-> file f (list f) (into path f) (assoc-in tree f value))))

(defn to-tree
  ([output] (to-tree output {"/" {}} []))
  ([output tree path]
   (let [current-line (first output)
         parts (-> current-line (or "") (str/split #" "))
         remaining-output (rest output)]
     (cond
       (nil? current-line) tree
       (str/starts-with? current-line "$ ls") (recur remaining-output tree path)
       (str/starts-with? current-line "$ cd") (recur remaining-output tree (resolve-cd path (last parts)))
       (str/starts-with? current-line "dir") (recur remaining-output (resolve-ls tree path (last parts)) path)
       :else (recur remaining-output (resolve-ls tree path (Integer/parseInt (first parts)) (last parts)) path)))))

(def tree (to-tree output))

(def dir-size (memoize (fn [tree dir]
                         (if (empty? tree) 0
                             (let [sub-tree (tree dir)
                                   file-size (->> sub-tree vals (filter number?) (reduce +))
                                   dir-names (->> sub-tree (filter #(coll? (second %))) (map first))]
                               (+ file-size (reduce + (map #(dir-size sub-tree %) dir-names))))))))

(def dir-sizes (memoize (fn [tree dir]
                          (if (empty? tree) (list 0 dir '())
                              (let [sub-tree (tree dir)
                                    file-size (->> sub-tree vals (filter number?) (reduce +))
                                    dir-names (->> sub-tree (filter #(coll? (second %))) (map first))
                                    sub-dir-sizes (map #(dir-sizes sub-tree %) dir-names)
                                    sub-dir-size (->> sub-dir-sizes (map first) (reduce +))]
                                (list (+ file-size sub-dir-size) dir sub-dir-sizes))))))

(pr (->> (dir-sizes tree "/")
         flatten
         (filter number?)
         (filter #(<= % 100000))
         (reduce +)))
