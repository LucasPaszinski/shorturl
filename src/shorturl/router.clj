(ns shorturl.router
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [ring.util.response :as resp]
            [shorturl.redirects :as redirects]))

(defn shorturl [req]
  (pretty/edn req)
  ;; (let [slug (redirects/create-short-link url)]
  {:status 200 :body {:slug "slug"}})

(defn slug->url [{{:keys [slug]}  :path-params :as req}]
  (pretty/edn req)
  (if-let [url (redirects/get-url-by-slug slug)]
    (resp/redirect (str "http://" url))
    {:status 404}))

(def router (ring/router
             [["/api/slug" {:post shorturl}]
              ["/:slug" {:get  slug->url}]]))

(def app
  (ring/ring-handler
   router
   {:exception pretty/exception
    :data {:muuntaja m/instance
           :middleware [swagger/swagger-feature
                        parameters/parameters-middleware
                        muuntaja/format-negotiate-middleware
                        muuntaja/format-response-middleware
                        (exception/create-exception-middleware
                         {::exception/default (partial exception/wrap-log-to-console exception/default-handler)})
                        muuntaja/format-request-middleware
                        coercion/coerce-response-middleware
                        coercion/coerce-request-middleware
                        multipart/multipart-middleware]}}))

(defn start []
  (jetty/run-jetty #'app {:port 3000, :join? false :ssl? false})
  (println "server running in port 3000"))


(comment
  (start))
