(ns linear)

    (defn check-vecs [vecs]
        (and (vector? vecs) (every? vector? vecs) (every? #(every? number? %) vecs) (every? #(== (count (first vecs)) (count %)) vecs)))
    
    (defn apply-mapv [f vecs]
        {:pre [(check-vecs (vec vecs))]}
        (apply mapv f vecs))
    
    (defn v+ [& vecs] 
        (apply-mapv + vecs))
    
    (defn v- [& vecs] 
        (apply-mapv - vecs))
    
    (defn v* [& vecs] 
        (apply-mapv * vecs))
    
    (defn vd [& vecs] 
        (apply-mapv / vecs))
    
    (defn dot [& vecs]
      (cond
        (empty? vecs) 0
        :else (apply + (apply-mapv * vecs))))
    
    (defn v*scal [v scal]
        {:pre [(check-vecs [v])]}
        (mapv #(* scal %) v))
    
    (defn v*s [v & s]
        (v*scal v (apply * s)))
    
    (defn m [f ms]
        {:pre [(every? check-vecs ms)]}
        (apply mapv #(apply-mapv f %&) ms))
    
    (defn m+ [& ms]
        (m + ms))
    
    (defn m- [& ms]
        (m - ms))
    
    (defn m* [& ms]
        (m * ms))
    
    (defn md [& ms]
        (m / ms))
    
    (defn m*s [m & s]
        {:pre [(check-vecs m)]}
        (mapv #(v*scal % (apply * s)) m))
    
    (defn transpose [m]
        (apply-mapv vector m))
    
    (defn m*v [m v]
        (mapv #(dot % v) m))
    
    (defn m1*m2 [m1 m2]
        (let [tm2 (transpose m2)] (mapv (fn [v1] (mapv (fn [v2] (dot v1 v2)) tm2)) m1)))
    
    (defn m*m [& ms] 
        (reduce m1*m2 (first ms) (rest ms)))
