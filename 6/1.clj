(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- empty-string? [token]
  (empty? (string/replace token #" " "")))

(defn- read-line-longs [line]
  (let [tokens (rest (string/split line #" "))
        numbers (filter #(not (empty-string? %)) tokens)]
    (map #(Long/parseLong (string/trim %)) numbers)))

(defn- zip [a b]
  (map vector a b))

(defn- read-time-to-distance [file-path]
  (with-open [reader (io/reader file-path)]
    (let [lines-seq (line-seq reader)
          times-line (string/trim (first lines-seq))
          distances-line (string/trim (second lines-seq))
          times (read-line-longs (first lines-seq))
          distances (read-line-longs (second lines-seq))]
      (zip times distances))))

(defn- hold-distances [t]
  (map #(* % (- t %)) (range 0 t)))

(defn- high-scores-count [[t distance]]
  (count (filter #(> % distance) (hold-distances t))))

(let [input (read-time-to-distance "input")]
  (println (reduce * (map high-scores-count input))))
