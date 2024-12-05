(ns linear)

(defn apply-mapv [f vecs]
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
    (apply + (apply-mapv * vecs)))

(defn v*s [v & s]
    (mapv #(* (apply * s) %) v))

(defn m [f ms]
    (apply-mapv #(apply-mapv f %&) ms))

(defn m+ [& ms]
    (m + ms))

(defn m- [& ms]
    (m - ms))

(defn m* [& ms]
    (m * ms))

(defn md [& ms]
    (m / ms))

(defn m*s [m & s]
    (mapv #(mapv (fn [el] (* el (apply * s))) %) m))

(defn transpose [m]
    (apply-mapv vector m))

(defn m*v [m v]
    (mapv #(dot % v) m))

(defn m1*m2 [m1 m2]
    (let [tm2 (transpose m2)] (mapv (fn [v1] (mapv (fn [v2] (dot v1 v2)) tm2)) m1)))

(defn m*m [& ms] 
    (reduce m1*m2 (first ms) (rest ms)))
