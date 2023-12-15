(load-file "1.clj")

(defn- mirror? [col]
  (let [reversed (reverse col)
        halfway (long (/ (count col) 2))]
    (loop [i 0
           difference false]
      (cond
        (= i halfway)
          difference
        (= (nth col i) (nth reversed i))
          (recur (inc i) difference)
        (not difference)
          (recur (inc i) true)
        :else
          false))))

(println (reduce + (map calculate (read-in "input"))))
