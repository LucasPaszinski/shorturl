(ns shorturl.server
  (:require [shorturl.router :as router])
  (:gen-class))

(defn -main []
  (router/start))
