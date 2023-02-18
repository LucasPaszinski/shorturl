(ns shorturl.kafka
  (:gen-class)
  (:import [org.apache.kafka.clients.admin AdminClientConfig NewTopic KafkaAdminClient]
           org.apache.kafka.clients.consumer.KafkaConsumer
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.common.serialization StringDeserializer StringSerializer]
           (java.time Duration)))

(defn uuid [] (.toString (java.util.UUID/randomUUID)))

(defn create-topics! [bootstrap-server topics ^Integer partitions ^Short replication]
  (let [config {AdminClientConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-server}
        adminClient (KafkaAdminClient/create config)
        new-topics (map (fn [^String topic-name]
                          (NewTopic. topic-name partitions replication)) topics)]
    (.createTopics adminClient new-topics)))

(defn build-consumer [bootstrap-server]
  (let [consumer-props
        {"bootstrap.servers",  bootstrap-server
         "group.id",           "example"
         "key.deserializer",   StringDeserializer
         "value.deserializer", StringDeserializer
         "auto.offset.reset",  "earliest"
         "enable.auto.commit", "true"}]
    (KafkaConsumer. consumer-props)))

(defn consumer-subscribe [consumer topic]
  (.subscribe consumer [topic]))

(defn build-producer ^KafkaProducer [bootstrap-server]
  (let [producer-props {"value.serializer"  StringSerializer
                        "key.serializer"    StringSerializer
                        "bootstrap.servers" bootstrap-server}]
    (KafkaProducer. producer-props)))


(comment
  ;; This is defined in the docker, don't forget to run docker first
  (def kafka-address "localhost:9092")
  (def consumer (build-consumer kafka-address))
  (def producer (build-producer kafka-address))
  (def topic "message-topic")

  (create-topics! kafka-address [topic] 1 1)

  (consumer-subscribe consumer topic)

  (for [_ (range 1 10000)]
    (.send producer (ProducerRecord. topic "a" "Hello from producers")))

  (for [msg (.poll consumer (Duration/ofMillis 1000))]
    {(.key msg) (.value msg)})

  ;;stop line wrap
  )
