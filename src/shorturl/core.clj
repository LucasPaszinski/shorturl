(ns shorturl.core
  (:require [shorturl.web :as web])
  (:gen-class))

(defn -main [] (web/start))