(load-file "1.clj")

(defn- less-than-range [part attribute amount destination]
  (let [[start end] (get part attribute)]
    (if (>= start amount)
      {:cont part}
      (let [destination-part (assoc part attribute [start (dec amount)])
            cont-part (assoc part attribute [amount end])]
        {destination destination-part :cont cont-part}))))

(defn- greater-than-range [part attribute amount destination]
  (let [[start end] (get part attribute)]
    (if (<= end amount)
      {:cont part}
      (let [destination-part (assoc part attribute [(inc amount) end])
            cont-part (assoc part attribute [start amount])]
        {destination destination-part :cont cont-part}))))

(defn- gen-rule [raw-rule]
  (if (not (string/includes? raw-rule ":"))
    (fn [sieve] {raw-rule sieve})
    (let [[raw-rule destination] (string/split raw-rule #":")]
      (if (string/includes? raw-rule "<")
        (let [[attribute raw-amount] (string/split raw-rule #"<")
              amount (Integer/parseInt raw-amount)]
          (fn [part] (less-than-range part attribute amount destination)))
        (let [[attribute raw-amount] (string/split raw-rule #">")
              amount (Integer/parseInt raw-amount)]
          (fn [part] (greater-than-range part attribute amount destination)))))))

(defn- run-rule [rule part-ranges]
  (loop [rule rule
         continuations []
         part-ranges part-ranges]
    (if (empty? rule)
      continuations
      (let [results (dissoc ((first rule) part-ranges) "R")
            part-ranges (get results :cont)
            continuations (mapcat #(conj continuations %) (vec (dissoc results :cont)))]
        (if (nil? part-ranges)
          continuations
          (recur (rest rule) continuations part-ranges))))))

(def starting-part-ranges {"x" [1 4000] "m" [1 4000] "a" [1 4000] "s" [1 4000]})

(defn- run-all [rules]
  (loop [accepted-ranges []
         continuations [["in" starting-part-ranges]]]
    (if (empty? continuations)
      accepted-ranges
      (let [[rule part-ranges] (first continuations)
            result (run-rule (get rules rule) part-ranges)
            accepts (map second (filter #(= "A" (first %)) result))
            accepted-ranges (reduce conj accepted-ranges accepts)]
        (recur accepted-ranges (reduce conj (rest continuations) (filter #(not= "A" (first %)) result)))))))

(defn- combinations [ranges]
  (let [r (fn [m attribute] (let [[start end] (get m attribute)] (- (inc end) start)))]
    (reduce * (map #(r ranges %) '("x" "m" "a" "s")))))

(let [[rules _] (read-in "test")
      accepted-ranges (run-all rules)]
  (println (reduce + (map combinations accepted-ranges))))
