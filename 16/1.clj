(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])
(require '[clojure.set     :as s])

(defn- address [grid [x y]]
  (nth (nth grid x []) y nil))

(defn- xy [beam]
  [(:x (:location beam)) (:y (:location beam))])

(defn- clockwise [beam]
  (let [[x y] (:direction beam)]
    (cond
      (and (zero? x) (= 1 y))  [1 0]
      (and (= 1 x) (zero? y))  [0 -1]
      (and (zero? x) (= -1 y)) [-1 0]
      (and (= -1 x) (zero? y)) [0 1])))

(defn- counter-clockwise [beam]
  (let [[x y] (:direction beam)]
    (cond
      (and (zero? x) (= 1 y))  [-1 0]
      (and (= -1 x) (zero? y)) [0 -1]
      (and (zero? x) (= -1 y)) [1 0]
      (and (= 1 x) (zero? y))  [0 1])))

(defn- re-id [beam]
  (assoc beam :id (str (java.util.UUID/randomUUID))))

(defn- to-path-entry [beam]
  {:location (:location beam) :direction (:direction beam)})

(defn- new-beam [starting-x starting-y direction-x direction-y]
  (let [location {:x starting-x :y starting-y}
        beam {:location location
              :direction [direction-x direction-y]}]
    (-> beam
      re-id
      (assoc :path (conj (set nil) (to-path-entry beam))))))

(defn- cycle? [beam]
  (contains? (:path beam) (to-path-entry beam)))

(def mirrors (set ["|" "/" "\\" "-"]))

(defn- should-halt? [grid beam]
  (let [is-cycle (cycle? beam)
        space (address grid (xy beam))]
    (or (nil? space)
        (and (contains? mirrors space) is-cycle))))

(defn- forward [beam [x y]]
  (let [[current-x current-y] (xy beam)]
    [(+ x current-x) (+ y current-y)]))

(defn- move [grid beam [x y]]
  (let [[new-x new-y] (forward beam [x y])
        new-location {:x new-x :y new-y}
        potential-beam (-> beam
                         (assoc :location new-location)
                         (assoc :direction [x y]))]
    (if (should-halt? grid potential-beam)
      [:halt beam]
      [:cont (assoc potential-beam :path (conj (:path beam) (to-path-entry potential-beam)))])))

(defn- tick [grid beam]
  (let [space (address grid (xy beam))]
    (if (= "." space)
      [(move grid beam (:direction beam))]
      (case space
        "/"
          (case (:direction beam)
            [0 1]  [(move grid beam (counter-clockwise beam))]
            [0 -1] [(move grid beam (counter-clockwise beam))]
            [1 0]  [(move grid beam (clockwise beam))]
            [-1 0] [(move grid beam (clockwise beam))])
        "\\"
          (case (:direction beam)
            [0 1]  [(move grid beam (clockwise beam))]
            [0 -1] [(move grid beam (clockwise beam))]
            [1 0]  [(move grid beam (counter-clockwise beam))]
            [-1 0] [(move grid beam (counter-clockwise beam))])
        "-"
          (if (= (abs (first (:direction beam))) 1)
            [(move grid beam (counter-clockwise beam))
             (move grid (re-id beam) (clockwise beam))]
            [(move grid beam (:direction beam))])
        "|"
          (if (= (abs (second (:direction beam))) 1)
            [(move grid beam (counter-clockwise beam))
             (move grid (re-id beam) (clockwise beam))]
            [(move grid beam (:direction beam))])))))

(defn- separate [beams]
  (let [groups (group-by first beams)]
    [(map second (:cont groups)) (map second (:halt groups))]))

(defn- tick-all [grid starting-beam]
  (loop [cont-beams {(:id starting-beam) starting-beam}
         halt-beams []]
    (if (empty? cont-beams)
      halt-beams
      (let [ticked (mapcat #(tick grid %) (vals cont-beams))
            [cont halt] (separate ticked)
            cont-beams (reduce #(assoc %1 (:id %2) %2) {} cont)
            cont-beams (reduce dissoc cont-beams (map :id halt))
            halt-beams (reduce conj halt-beams halt)]
        (recur cont-beams halt-beams)))))

(defn- read-in [file-path]
  (with-open [f (io/reader file-path)]
    (doall (map #(string/split (string/trim %) #"") (line-seq f)))))

(let [in (read-in "input")
      halt-beams (tick-all in (new-beam 0 0 0 1))
      locations (map :location (mapcat :path halt-beams))
      unique-locations (set locations)]
  (println (count unique-locations)))
