(ns user
  (:require [ragtime.jdbc :as jdbc]
            [shorturl.db :as db]
            [ragtime.repl :as rag]))

(def datastore (jdbc/sql-database db/mysql-db))

(defn config []
  {:datastore  datastore
   :migrations (jdbc/load-directory "./migrations")})

(defn migrate []
  (rag/migrate (config)))

(defn rollback []
  (rag/rollback (config)))

(comment
  (migrate)
  (rollback))

