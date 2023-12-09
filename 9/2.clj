(load-file "1.clj")

(defn- prepend-value [row next-row]
  (let [value (- (first row) (first next-row))]
    (cons value row)))

(defn- historical-rows [rows]
  (let [rows (reverse rows)]
    (loop [n 0
           extrapolations []]
      (cond
        (= n (count rows))
          (reverse extrapolations)
        (zero? n)
          (let [row (nth rows n)]
            (recur (inc n) (conj extrapolations (cons 0 row))))
        :else
          (let [row (nth rows n)
                previous-row (last extrapolations)
                new-row (prepend-value row previous-row)]
           (recur (inc n) (conj extrapolations new-row)))))))

(defn- find-new-value-historical [rows]
  (-> (historical-rows rows) first first))

(let [in (read-in "input")
      results (map #(extrapolate % find-new-value-historical) in)]
  (println (reduce + results)))
