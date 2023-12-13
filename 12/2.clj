(load-file "1.clj")

(defn- build-expanded-row [line]
  (let [[state-raw constraints-raw] (string/split line #" ")
        state (string/split state-raw #"")
        expanded-state (concat (reduce concat (map #(conj % "?") (repeat 4 state))) state)
        constraints (map #(Long/parseLong %) (string/split constraints-raw #","))
        expanded-constraints (reduce concat (repeat 5 constraints))]
    (gen-state-machine expanded-state expanded-constraints)))

(let [rows (read-in "test" build-expanded-row)]
  (println (reduce + (pmap count-possibilities rows))))

(shutdown-agents)
