(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- boulder? [c]
  (= c "#"))

(defn- not-boulder? [c]
  (not (boulder? c)))

(defn- rock? [c]
  (= c "O"))

(defn- fill-space [remaining]
  (let [before-boulder (take-while not-boulder? remaining)
        rocks (count (filter rock? before-boulder))
        spaces (- (count before-boulder) rocks)
        transient-segment (transient [])]
    (do
      (dotimes [_ rocks] (conj! transient-segment "O"))
      (dotimes [_ spaces] (conj! transient-segment "."))
      (persistent! transient-segment))))

(defn- slide [col]
  (loop [remaining col
         new-row []]
    (cond
      (empty? remaining)
        new-row
      (boulder? (first remaining))
        (recur (subvec remaining 1) (conj new-row "#"))
      :else
        (let [segment (fill-space remaining)
              size (count segment)]
          (recur (subvec remaining size) (vec (concat new-row segment)))))))

(defn- transpose [plane]
  (apply mapv vector plane))

(defn slide-north [plane]
  (map slide plane))

(defn count-load [slide-plane]
  (loop [i 0
         multiplier (count slide-plane)
         c 0]
    (if (= i (count slide-plane))
      c
      (let [rocks (count (filter rock? (nth slide-plane i)))
            l (* multiplier rocks)]
        (recur (inc i) (dec multiplier) (+ c l))))))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (doall (map #(string/split (string/trim %) #"") (line-seq f)))))

(-> (read-in "input")
  transpose
  slide-north
  transpose
  transpose
  transpose
  count-load
  println)
