(ns shorturl.rabbitmq
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lco]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [clojure.tools.reader.edn :as edn]))

(def queue-name "my-queue")
(def ^{:const true} exchange "")
(def direct-exchange "direct-exchange")
(def topic-exchange "topic-exchange")

(defn decode-str [msg]
  (->> msg
       (map char)
       (reduce str "")))

(defn msg-handler [chan meta msg]
  (clojure.pprint/pprint (edn/read-string msg)))

(comment
  ;; declare channel
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lq/declare chan queue-name {:exclusive false :auto-delete false}))

  ;; publish msg to queue
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lb/publish chan exchange queue-name  (prn-str {:data "hello from app"})))

  ;; subscribe on queue
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lco/subscribe chan queue-name #'msg-handler {:auto-ack true}))

  ;; create direct exchange
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (le/declare chan direct-exchange "direct")
    (lq/bind chan queue-name direct-exchange {:routing-key "direct-route"}))

  ;; publish to direct route
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lb/publish chan direct-exchange "direct-route" (prn-str {:data "hello from direct queue"})))

  ;; subscribe on queue
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lco/subscribe chan queue-name #'msg-handler {:routing-key "direct-route"}))

    ;; create topic exchange
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (le/declare chan topic-exchange "topic")
    (lq/bind chan queue-name topic-exchange {:routing-key "topic.*"}))

  ;; publish to topic route
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lb/publish chan topic-exchange "topic.banana" (prn-str {:data "hello from topic banana queue"}))
    (lb/publish chan topic-exchange "topic.tomato" (prn-str {:data "hello from topic tomato queue"})))

  ;; subscribe topic on queue
  (with-open [conn (lc/connect)
              chan (lch/open conn)]
    (lco/subscribe chan queue-name #'msg-handler {:routing-key "topic"}))
  ;; stop
  )