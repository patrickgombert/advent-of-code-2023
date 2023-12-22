(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- address [plane [x y]]
  (nth (nth plane x []) y nil))

(defn- next-steps [plane [x y]]
  (let [candidates [[(inc x) y]
                    [(dec x) y]
                    [x (inc y)]
                    [x (dec y)]]]
    (filter #(= "." (address plane %)) candidates)))

(defn- walk [plane starting-location steps]
  (loop [locations [starting-location]
         steps     steps]
    (if (zero? steps)
      locations
      (let [neighbors (mapcat #(next-steps plane %) locations)]
        (recur (into (set '()) neighbors) (dec steps))))))

(defn- find-s [plane]
  (let [result (for [[x row] (map-indexed vector plane)
                     [y space] (map-indexed vector row)
                     :when (= "S" space)]
                [x y])]
    (first result)))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (doall (map #(string/split (string/trim %) #"") (line-seq f)))))

(let [plane (read-in "input")
      s (find-s plane)]
  (println (inc (count (walk plane s 64)))))
