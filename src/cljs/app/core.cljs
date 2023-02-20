(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [promesa.core :as p]))

(defn create-short-url [url]
  (p/let [res (js/fetch "/api/shorty" #js {:method "POST"
                                           :body "{\"url\":\"www.google.com/oi\"}"})
          jres (.json res)]
    (js/console.log jres)))

(create-short-url "www.google.com")

(defn app []
  (let [[state set-state] (hooks/use-state {:name ""})]
    (d/div
     (d/input {:value (:name state)
               :on-change #(set-state assoc :name (.. % -target -value))})
     (d/button "Shorten Url!" {:on-click ()}))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (.render root ($ app)))