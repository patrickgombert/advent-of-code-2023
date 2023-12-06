(require '[clojure.java.io :as io])
(require '[clojure.string  :as string])

(defn- parse-seeds [acc seeds-row]
  (let [seeds-str (string/trim
                    (second (string/split seeds-row #":")))
        tokens    (string/split seeds-str #" ")]
    (assoc acc :seeds (map #(Long/parseLong %) tokens))))

(defn- parse-state [acc row state]
  (let [acc (if (nil? (state acc))
              (assoc acc state [])
              acc)
        range-values
          (map #(Long/parseLong %) (string/split row #" "))]
    (assoc acc state (cons range-values (state acc)))))

(defn- set-parsing-state [line]
  (-> (string/split line #" ") first keyword))

(defn- readlines-almanac [file-path]
  (with-open [reader (io/reader file-path)]
    (loop [lines (line-seq reader)
            acc {}
            parsing-state :seeds]
      (let [line (string/trim (first lines))]
        (cond
          (nil? (next lines))
            acc
          (empty? line)
            (recur (next lines) acc nil)
          (nil? parsing-state)
            (recur (next lines) acc (set-parsing-state line))
          (= parsing-state :seeds)
            (recur (next lines) (parse-seeds acc line) nil)
          :else
            (recur (next lines) (parse-state acc line parsing-state) parsing-state))))))

(defn- in-range [source [_ s increment]]
  (and (>= source s) (< source (+ s increment))))

(defn- map-value [source mappings]
  (let [match (first (filter #(in-range source %) mappings))]
    (if (nil? match)
      source
      (let [difference (- source (second match))]
        (+ (first match) difference)))))

(defn- map-location [data seed-number]
  (-> seed-number
    (map-value (:seed-to-soil data))
    (map-value (:soil-to-fertilizer data))
    (map-value (:fertilizer-to-water data))
    (map-value (:water-to-light data))
    (map-value (:light-to-temperature data))
    (map-value (:temperature-to-humidity data))
    (map-value (:humidity-to-location data))))

(let [data (readlines-almanac "input")]
  (println (apply min (map #(map-location data %) (:seeds data)))))
