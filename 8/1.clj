(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- to-direction [line]
  (map #(case % "L" :left "R" :right) (string/split line #"")))

(defn- to-link [line]
  (let [[k vs] (string/split (string/trim line) #" = ")
        vs (string/replace (string/replace vs #"\(" "") #"\)" "")
        vs (map keyword (string/split vs #", "))]
    {(keyword k) {:left (first vs) :right (second vs)}}))

(defn- to-links [lines]
  (reduce merge {} (map to-link lines)))

(defn- parse-input [file-path]
  (with-open [f (io/reader file-path)]
    (let [lines (line-seq f)
          paths (to-direction (string/trim (first lines)))]
      [paths (to-links (rest (rest lines)))])))

(defn- take-step [links direction location]
  (let [choices (location links)]
    (direction choices)))

(defn- count-steps [input start end]
  (let [links (second input)]
    (loop [directions (cycle (first input))
           location start
           steps 0]
      (let [next-location (take-step links (first directions) location)
            next-steps (inc steps)]
        (if (string/ends-with?  next-location end)
          next-steps
          (recur (next directions) next-location next-steps))))))

(println (count-steps (parse-input "input") :AAA "ZZZ"))
