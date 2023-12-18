(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(def infinity Integer/MAX_VALUE)

(defn- find-first [pred col]
  (loop [col col]
    (cond
      (empty? col) nil
      (pred (first col)) (first col)
      :else (recur (rest col)))))

(defn- address [grid x y]
  (nth (nth grid x []) y nil))

(defn- do-turn [grid node direction distance turn-fn]
  (let [[dx dy] (turn-fn direction)
        neighbor-x (+ (first node) dx)
        neighbor-y (+ (second node) dy)
        weight (address grid neighbor-x neighbor-y)]
    (if (nil? weight)
      nil
      (let [distance (+ distance weight)]
        {[neighbor-x neighbor-y] [distance 0 [dx dy]]}))))

(defn- clockwise [grid node direction distance]
  (do-turn grid node direction distance
           (fn [[x y]] (cond
                         (and (zero? x) (= 1 y))  [1 0]
                         (and (= 1 x) (zero? y))  [0 -1]
                         (and (zero? x) (= -1 y)) [-1 0]
                         (and (= -1 x) (zero? y)) [0 1]))))

(defn- counter-clockwise [grid node direction distance]
  (do-turn grid node direction distance
           (fn [[x y]] (cond
                         (and (zero? x) (= 1 y))  [-1 0]
                         (and (= -1 x) (zero? y)) [0 -1]
                         (and (zero? x) (= -1 y)) [1 0]
                         (and (= 1 x) (zero? y))  [0 1]))))

(defn- forward [grid node forwards direction distance]
  (let [neighbor-x (+ (first node) (first direction))
        neighbor-y (+ (second node) (second direction))
        weight (address grid neighbor-x neighbor-y)]
    (if (nil? weight)
      nil
      (let [distance (+ distance weight)]
        {[neighbor-x neighbor-y] [distance (inc forwards) direction]}))))

(defn- neighbors [grid forwards node direction distance]
  (if (= [0 0] direction)
    (filter #(not (nil? %))
            (merge
              (forward grid node 0 [0 1] distance)
              (forward grid node 0 [0 -1] distance)
              (forward grid node 0 [1 0] distance)
              (forward grid node 0 [-1 0] distance)))
    (let [clockwise (clockwise grid node direction distance)
          counter-clockwise (counter-clockwise grid node direction distance)]
      (if (= 3 forwards)
        (filter #(not (nil? %)) (merge clockwise counter-clockwise))
        (filter #(not (nil? %)) (merge clockwise counter-clockwise (forward grid node forwards direction distance)))))))

(defn- update? [v1 v2]
  (if (not= (first v1) (first v2))
    (< (first v1) (first v2))
    (< (second v1) (second v2))))

(defn- update-distances [tentative-distance neighbors]
  (loop [neighbors neighbors
         tentative-distance tentative-distance]
    (if (empty? neighbors)
      tentative-distance
      (let [[node values] (first neighbors)
            existing-values (get tentative-distance node)]
        (recur (rest neighbors)
               (if (update? values existing-values)
                 (assoc tentative-distance node values)
                 tentative-distance))))))

(defn- dijkstra [grid unvisited tentative-distance]
  (let [end-x (dec (count grid))
        end-y (dec (count (first grid)))]
    (loop [unvisited unvisited
           tentative-distance tentative-distance]
      (let [sorted (sort-by #(first (second %)) tentative-distance)
            entry (find-first #(contains? unvisited (first %)) sorted)
            node (first entry)
            [distance forwards direction] (second entry)]
      (if (and (= (first node) end-x) (= (second node) end-y))
        distance
        (let [neighbors (neighbors grid forwards node direction distance)
              neighbors (filter #(contains? unvisited (first %)) neighbors)
              tentative-distance (update-distances tentative-distance neighbors)]
          (recur (disj unvisited node) tentative-distance)))))))

(defn- read-in-line [line]
  (map #(Long/parseLong %) (string/split (string/trim line) #"")))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (doall (map read-in-line (line-seq f)))))

(defn- run [file-path]
  (let [grid (read-in file-path)
        unvisited (set (mapcat (fn [x] (map (fn [y] [x y]) (range (count (first grid))))) (range (count grid))))
        ; location => [distance forwards direction]
        tentative-distance (reduce #(assoc %1 %2 [infinity 0 [0 1]]) {} unvisited)
        tentative-distance (assoc tentative-distance [0 0] [0 0 [0 0]])]
    (println (dijkstra grid unvisited tentative-distance))))

(run "input")
