(ns shorturl.router
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.util.response :as resp]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [shorturl.redirects :as redirects])
  (:gen-class))

(def app
  (ring/ring-handler
   (ring/router
    [["/api/shorty"
      {:post {:summary "create a new slug for redirecting the url"
              :parameters {:body {:url string?}}
              :responses {201 {:body {:slug string?}}}
              :handler (fn [{{{:keys [url]} :body} :parameters :as req}]
                         (resp/created "/api/shorty" {:slug (redirects/create-short-link url)}))}}]

     ["/:slug"
      {:get {:summary "given a valid slug redirects the user to url"
             :parameters {:path-params {:slug string?}}
             :handler (fn [{{:keys [slug]} :path-params :as req}]
                        (if-let [url (redirects/get-url-by-slug slug)]
                          (resp/redirect (str "http://" url))
                          (resp/not-found {})))}}]]

    {:conflicts nil
     :exception pretty/exception
     :data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         (exception/create-exception-middleware
                          {::exception/default (partial exception/wrap-log-to-console
                                                        exception/default-handler)})
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware
                         multipart/multipart-middleware]}})))


(defn start []
  (jetty/run-jetty #'app {:port 3000, :join? false})
  (println "server running in port 3000"))

(defn -main []
  (start))

(comment
  (def server (start))
  (.stop server))
