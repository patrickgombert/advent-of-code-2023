(load-file "1.clj")

(defn- split-range [[[seed-start seed-end] offset] [dest source increment]]
  (let [seed-range-start (+ seed-start offset)
        seed-range-end (+ seed-end offset)
        range-start source
        range-end (+ source increment)
        difference (+ offset (- dest source))]
    (cond
      (and (>= seed-range-start range-start) (< seed-range-end range-end))
        {[seed-start seed-end] difference}
      (and (< seed-range-start range-start) (> seed-range-end range-end))
        {[seed-start (- (dec range-start) offset)] offset
         [(- range-start offset) (- (dec range-end) offset)] difference
         [(- range-end offset) seed-end] offset}
      (and (< seed-range-start range-start) (>= seed-range-end range-start))
        {[seed-start (- (dec range-start) offset)] offset
         [(- range-start offset) seed-end] difference}
      (and (< seed-range-start range-end) (>= seed-range-end range-end))
        {[seed-start (- (dec range-end) offset)] difference
         [(- range-end offset) seed-end] offset}
      :else nil)))

(defn- overlap-range [seed-range mappings]
  (let [overlap-seed-range (filter #(not (nil? %)) (map #(split-range seed-range %) mappings))
        reconstructed-seed-range {(first seed-range) (second seed-range)}]
    (reduce merge reconstructed-seed-range overlap-seed-range)))

(defn- map-override-values [seed-ranges mappings]
  (let [overlap-mappings (map #(overlap-range % mappings) seed-ranges)]
    (reduce merge {} overlap-mappings)))

(defn- map-range-location [data seed-range]
  (-> {seed-range 0}
    (map-override-values (:seed-to-soil data))
    (map-override-values (:soil-to-fertilizer data))
    (map-override-values (:fertilizer-to-water data))
    (map-override-values (:water-to-light data))
    (map-override-values (:light-to-temperature data))
    (map-override-values (:temperature-to-humidity data))
    (map-override-values (:humidity-to-location data))))

(defn- to-range [[start increment]]
  [start (dec (+ start increment))])

(defn- location [[k v]]
  (+ (first k) v))

(defn- min-range-location [seed-ranges]
  (apply min (map location seed-ranges)))

(let [data (readlines-almanac "input")
      seed-ranges (map to-range (partition 2 (:seeds data)))
      range-locations (map #(map-range-location data %) seed-ranges)]
  (println (apply min (map min-range-location range-locations))))
