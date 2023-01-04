(ns shorturl.db
  (:require [clojure.java.jdbc :as j]
            [clojure.edn :as edn]))

(def mysql-db (:db (edn/read-string (slurp ".env.edn"))))

(defn query [q]  (j/query mysql-db q))
(defn insert [q] (j/db-do-prepared mysql-db q))


