(load-file "1.clj")

(defn- neighbors [grid forwards node direction distance]
  (if (= [0 0] direction)
    (filter #(not (nil? %))
            (merge
              (forward grid node 0 [0 1] distance)
              (forward grid node 0 [0 -1] distance)
              (forward grid node 0 [1 0] distance)
              (forward grid node 0 [-1 0] distance)))
    (cond
      (< 4 forwards)
        (filter #(not (nil? %)) (forward grid node forwards direction distance))
      (= 10 forwards)
        (filter #(not (nil? %))
                (merge
                  (clockwise grid node direction distance)
                  (counter-clockwise grid node direction distance)))
      :else
        (filter #(not (nil? %))
                (merge
                  (clockwise grid node direction distance)
                  (counter-clockwise grid node direction distance)
                  (forward grid node forwards direction distance))))))

(run "input")
