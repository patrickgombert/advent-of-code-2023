(load-file "1.clj")

(defn gcd [a b]
  (loop [a (abs a)
         b (abs b)]
    (if (zero? b)
      a
      (recur b (mod a b)))))

(defn lcm [a b]
  (* b (quot a (gcd a b))))

(defn- count-ghost-steps [input]
  (let [starting-locations (filter #(string/ends-with? % "A") (keys (second input)))
        steps (map #(count-steps input % "Z") starting-locations)]
    (reduce lcm steps)))

(-> (parse-input "input") count-ghost-steps println)
