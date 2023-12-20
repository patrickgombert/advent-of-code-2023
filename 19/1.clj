(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- gen-rule [raw-rule]
  (if (not (string/includes? raw-rule ":"))
    (fn [_] raw-rule)
    (let [[raw-rule destination] (string/split raw-rule #":")]
      (if (string/includes? raw-rule "<")
        (let [[attribute raw-amount] (string/split raw-rule #"<")
              amount (Integer/parseInt raw-amount)]
          (fn [part] (if (< (get part attribute) amount) destination)))
        (let [[attribute raw-amount] (string/split raw-rule #">")
              amount (Integer/parseInt raw-amount)]
          (fn [part] (if (> (get part attribute) amount) destination)))))))

(defn- read-rule [line]
  (let [[rule-name rest-raw] (string/split (subs line 0 (dec (count line))) #"\{")
        raw-rules (string/split rest-raw #",")]
    {rule-name (map gen-rule raw-rules)}))

(defn- read-part [line]
  (let [raw-categories (string/split (subs line 1 (dec (count line))) #",")
        kvs (map #(string/split % #"=") raw-categories)]
    (reduce #(assoc %1 (first %2) (Integer/parseInt (second %2))) {} kvs)))

(defn- handle-line [[reading-rules rules parts] raw-line]
  (let [line (string/trim raw-line)]
    (cond
      (empty? line) [false rules parts]
      (true? reading-rules) [true (merge rules (read-rule line)) parts]
      :else [false rules (conj parts (read-part line))])))

(defn- sort-part [rules part]
  (loop [rule (get rules "in")]
    (let [next-rule (some #(%1 part) rule)]
      (if (or (= "A" next-rule) (= "R" next-rule))
        next-rule
        (recur (get rules next-rule))))))

(defn- sum-part [part]
  (reduce + (map #(get part %) '("x" "m" "a" "s"))))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (let [[_ rules parts] (reduce handle-line [true {} []] (line-seq f))]
      [rules parts])))

(let [[rules parts] (read-in "input")
      sorted-parts (group-by #(sort-part rules %) parts)]
  (println (reduce + (map sum-part (get sorted-parts "A")))))
