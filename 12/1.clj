(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- updating [state-machine & args]
  (let [kvs (partition 2 args)]
    (reduce #(assoc %1 (first %2) (second %2)) state-machine kvs)))

(defn- for-working [state-machine next-state]
  (if (not (zero? (:remaining-constraint state-machine)))
    ; working when we need to see more not working
    [:invalid]
    ; continue
    [(updating state-machine
               :state next-state
               :previous-character ".")]))

(defn- for-broken [state-machine next-state]
  (if
    (not (zero? (:remaining-constraint state-machine)))
      ; existing constraint
      [(updating state-machine
                 :state next-state
                 :previous-character "#"
                 :remaining-constraint (dec (:remaining-constraint state-machine)))]
      ; new constraint
      (if (= (:previous-character state-machine) "#")
        ; we need a space between broken springs
        [:invalid]
        (let [new-constraint (first (:constraints state-machine))
              remaining-constraints (rest (:constraints state-machine))]
          (if (nil? new-constraint)
            [:invalid]
            [(updating state-machine
                      :state next-state
                      :previous-character "#"
                      :remaining-constraint (dec new-constraint)
                      :constraints remaining-constraints)])))))

(defn- for-unknown [state-machine next-state]
  (concat
    (for-working state-machine next-state)
    (for-broken state-machine next-state)))

(defn- next-states [state-machine]
  (let [next-character (first (:state state-machine))
        next-state (rest (:state state-machine))]
    (cond
      ; potentially satisfied
      (and (zero? (:remaining-constraint state-machine)) (empty? (:constraints state-machine)))
        ; if we have any broken left then this is incorrect
        (if (some #(= "#" %) (:state state-machine))
          [:invalid]
          ; otherwise it's valid
          [:terminal])
      ; not-satisfied - end of string
      (nil? next-character)
        [:invalid]
      ; continue iterating to the next states
      :else
        (case next-character
          "."
            (for-working state-machine next-state)
          "#"
            (for-broken state-machine next-state)
          "?"
            (for-unknown state-machine next-state)))))

(defn- gen-state-machine [state constraints]
  {:state state
   :constraints constraints
   :remaining-constraint 0
   :previous-character "."})

(defn- build-row [line]
  (let [[state-raw constraints-raw] (string/split line #" ")
        state (string/split state-raw #"")
        constraints (map #(Long/parseLong %) (string/split constraints-raw  #","))]
    (gen-state-machine state constraints)))

(defn- read-in [file-path row-builder]
  (with-open [f (io/reader file-path)]
    (doall (map row-builder (map string/trim (line-seq f))))))

(defn- count-possibilities [state-machine]
  (loop [state-machines [state-machine]
         total          0]
    (let [head (first state-machines)
          tail (rest state-machines)]
      (if (nil? head)
        total
        (let [new-states (next-states head)
              terminal (count (filter #(= :terminal %) new-states))
              to-append (filter map? new-states)]
          (recur (reduce conj tail to-append) (+ total terminal)))))))

(let [rows (read-in "input" build-row)]
  (println (reduce + (map count-possibilities rows))))
