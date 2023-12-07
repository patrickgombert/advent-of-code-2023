(load-file "1.clj")

(defn- read-long-line [line]
  (let [value (second (string/split line #":"))]
    (Long/parseLong (string/replace value #" " ""))))

(defn- read-time-and-distance [file-path]
  (with-open [reader (io/reader file-path)]
    (let [lines-seq (line-seq reader)
          t (read-long-line (first lines-seq))
          distance (read-long-line (second lines-seq))]
      [t distance])))

(println (-> (read-time-and-distance "input") high-scores-count))
