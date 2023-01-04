(ns shorturl.redirects
  (:require [honey.sql :as sql]
            [shorturl.db :as db]))

(defn index-redirects []
  (->> {:select :* :from :redirects}
       (sql/format)
       (db/query)))

(defn get-by-slug [slug]
  (->> {:select [:url]
        :where [:= :slug slug]
        :from :redirects}
       (sql/format)
       (db/query)
       (first)
       (:url)))

(defn create-short-link [slug url]
  (->> {:insert-into [:redirects]
        :values [{:slug slug :url url}]}
       (sql/format)
       (db/insert)))

(comment
  (create-short-link "abc" "https//www.google.com")
  (get-by-slug "abc")
  (index-redirects))
