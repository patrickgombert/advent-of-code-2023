(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- split-line [line]
  (let [split (string/split (string/trim line) #" ")]
    (map #(Long/parseLong %) split)))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (doall (map split-line (line-seq f)))))

(defn- find-new-value [extrapolations]
  (reduce + (map last extrapolations)))

(defn- gen-next-row [row]
  (loop [v (first row)
         remainder (rest row)
         result []]
    (let [next-v (first remainder)]
      (if (nil? next-v)
        result
        (recur next-v (rest remainder) (conj result (- next-v v)))))))

(defn- extrapolate [values new-value-f]
  (loop [extrapolations [values]]
    (let [last-row (last extrapolations)]
      (if (every? zero? last-row)
        (new-value-f extrapolations)
        (recur (conj extrapolations (gen-next-row last-row)))))))

(let [in (read-in "input")
      results (map #(extrapolate % find-new-value) in)]
  (println (reduce + results)))
