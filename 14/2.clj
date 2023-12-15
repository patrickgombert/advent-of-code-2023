(load-file "1.clj")

(defn- -rotate [plane]
  (let [reversed (map reverse plane)]
    (transpose reversed)))

(defn rotate [plane]
  (let [transposed (transpose plane)]
    (mapv reverse transposed)))

(defn- spin [plane]
  (-> plane
    ;north
    transpose
    slide-north
    ;west
    -rotate
    slide-north
    ;south
    -rotate
    slide-north
    ; east
    -rotate
    slide-north
    (#(mapv reverse %))))

(defn spin-cycles [plane times]
  (loop [i 1
         spin-plane plane
         history-index-plane {}
         history-hash-index {}]
    (if (= i times)
      spin-plane
      (let [next-spin-plane (spin spin-plane)
            h (hash next-spin-plane)]
        (if (nil? (get history-hash-index h))
          (recur (inc i) next-spin-plane (assoc history-index-plane i next-spin-plane) (assoc history-hash-index h i))
          (let [previous-i (get history-hash-index h)
                count-in-cycle (- i previous-i)
                count-load-i (+ previous-i (rem (- times previous-i) count-in-cycle))]
            (get history-index-plane count-load-i)))))))

(println (count-load (spin-cycles (read-in "input") 1000000000)))
