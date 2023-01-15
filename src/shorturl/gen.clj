(ns shorturl.gen
  (:require [clojure.string :as str]))

(def base 62)
(def to-base62 (str/split "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" #""))

(defn encode [n]
  (loop [n (dec n)
         a ""]
    (let [div (quot n base)
          mod (rem n base)]
      (if (zero? div)
        (str (to-base62 mod) a)
        (recur div (str (to-base62  mod) a))))))

(defn decode [slug]
  (->> (str/split slug #"")
       (map #(.indexOf to-base62 %1))
       (reverse)
       (map-indexed (fn [idx itm] [idx itm]))
       (reduce (fn [acc [idx itm]] (+ acc (* itm (Math/pow 62 idx)))) 0)
       (int)
       (inc)))
