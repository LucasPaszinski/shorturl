(ns shorturl.redirects
  (:require [clojure.string :as s]
            [honey.sql :as sql]
            [shorturl.db :as db]
            [shorturl.gen :refer [encode decode]]))

(defn index-redirects []
  (->> {:select :* :from :redirects}
       (sql/format)
       (db/query)))

(defn get-by [key val]
  (->> {:select [:*]
        :where [:= key val]
        :from :redirects}
       (sql/format)
       (db/query)))

(defn get-url-by-slug [slug]
  (->> (decode slug)
       (get-by :id)
       (first)
       (:url)))

(defn get-slug-by-url [url]
  (->> (get-by :url url)
       (first)
       (:id)
       (encode)))

(defn- prepare-url [url]
  (cond (s/starts-with? url "https://") (s/replace-first url "https://" "")
        (s/starts-with? url "http://") (s/replace-first url "http://" "")
        :else url))

(defn- insert-short-link [url]
  (->> {:insert-into [:redirects]
        :values [{:url (prepare-url url)}]}
       (sql/format)
       (db/insert)))

(defn create-short-link [url]
  (try (encode (insert-short-link url))
       (catch Exception _ (get-slug-by-url url))))

(comment (create-short-link "www.github.com/LucasPaszinski/aoc22"))
(comment (get-url-by-slug "9"))
(comment (index-redirects))
