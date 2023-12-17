(load-file "1.clj")

(defn- create-task [grid x y dx dy]
  (let [starting-beam (new-beam x y dx dy)]
    (fn [] (let [halt-beams (tick-all grid starting-beam)
                 locations (map :location (mapcat :path halt-beams))
                 unique-locations (set locations)]
             (count unique-locations)))))

(defn- create-tasks [grid]
  (let [tasks (transient [])]
    (do
      ; top-left
      (conj! tasks (create-task grid 0 0 0 1))
      (conj! tasks (create-task grid 0 0 1 0))
      ; top-right
      (conj! tasks (create-task grid 0 (dec (count (first grid))) 0 -1))
      (conj! tasks (create-task grid 0 (dec (count (first grid))) 1 0))
      ; bottom-left
      (conj! tasks (create-task grid (dec (count grid)) 0 0 1))
      (conj! tasks (create-task grid (dec (count grid)) 0 -1 0))
      ; bottom-right
      (conj! tasks (create-task grid (dec (count grid)) (dec (count (first grid))) 0 -1))
      (conj! tasks (create-task grid (dec (count grid)) (dec (count (first grid))) -1 0))
      ; top-row
      (dotimes [i (- (count (first grid)) 3)]
        (conj! tasks (create-task grid 0 (inc i) 1 0)))
      ; bottom-row
      (dotimes [i (- (count (first grid)) 3)]
        (conj! tasks (create-task grid 0 (inc i) -1 0)))
      ; left-column
      (dotimes [i (- (count grid) 3)]
        (conj! tasks (create-task grid (inc i) 0 0 1)))
      ; right-column
      (dotimes [i (- (count grid) 3)]
        (conj! tasks (create-task grid (inc i) 0 0 -1)))
      (persistent! tasks))))

;12_104 total tasks
(let [in (read-in "input")
      executor (java.util.concurrent.Executors/newFixedThreadPool 2048)
      futures (.invokeAll executor (create-tasks in))
      results (map #(.get %) futures)]
  (do
    (.shutdown executor)
    (println (reduce max results))))
