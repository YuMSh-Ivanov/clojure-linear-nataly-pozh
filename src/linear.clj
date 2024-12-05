(ns linear)

    (defn check-vecs [vecs]
        (and (coll? vecs) (every? #(every? number? %) vecs) (every? #(== (count (first vecs)) (count %)) vecs)))

    (defn check-matr [matrs]
        (and (coll? matrs) (every? vector? matrs) (every? check-vecs matrs) (every? #(== (count (first matrs)) (count %)) matrs)))

    (defn apply-mapv [f vecs]
        {:pre [(check-vecs vecs)]}
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
        {:pre [(and (check-vecs [v]) (every? number? scal))]}
        (mapv #(* (apply * scal) %) v))
    
    (defn v*s [v & s]
        (v*scal v s))
    
    (defn m [f ms]
        {:pre [(check-matr ms)]}
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
        {:pre [(vector? m)]}
        (mapv #(v*scal % s) m))
    
    (defn transpose [m]
        (apply-mapv vector m))
    
    (defn m*v [m v]
        (mapv #(dot % v) m))
    
    (defn m1*m2 [m1 m2]
        (let [tm2 (transpose m2)] (mapv (fn [v1] (mapv (fn [v2] (dot v1 v2)) tm2)) m1)))
    
    (defn m*m [& ms] 
        (reduce m1*m2 (first ms) (rest ms)))
