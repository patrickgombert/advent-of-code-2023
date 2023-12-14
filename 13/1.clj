(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- mirror? [col]
  (let [reversed (reverse col)
        halfway (long (/ (count col) 2))]
    (loop [i 0]
      (cond
        (= i halfway)
          true
        (= (nth col i) (nth reversed i))
          (recur (inc i))
        :else
          false))))

(defn- make-mirror-candidate [row i]
  (let [over-half (> i (/ (count row) 2))]
    (if (not over-half)
      (subvec row 0 (* i 2))
      (-> (vec (reverse row))
        (#(subvec % 0 (* (- (dec (count row)) (dec i)) 2)))
        reverse
        vec))))

(defn- vertical-reflection-index [pattern]
  (let [size (count (first pattern))]
    (loop [i 1]
      (cond
        (= i size)
          0
        :else
          (let [rows (map #(make-mirror-candidate % i) pattern)]
            (if (every? mirror? rows)
              i
              (recur (inc i))))))))

(defn- horizontal-reflection-index [pattern]
  (let [transposed (apply mapv vector pattern)]
    (vertical-reflection-index transposed)))

(defn- calculate [pattern]
  (let [vertical (vertical-reflection-index pattern)
        horizontal (horizontal-reflection-index pattern)]
    (+ vertical (* 100 horizontal))))

(defn- read-row [[acc current] raw-row]
  (let [row (string/split (string/trim raw-row) #"")]
    (if (empty? raw-row)
      [(conj acc current) []]
      [acc (conj current row)])))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (let [[acc current] (reduce read-row [[] []] (line-seq f))]
      (conj acc current))))

(println (reduce + (map calculate (read-in "input"))))
