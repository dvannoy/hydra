package hydra.kafka

import hydra.kafka.util.KafkaUtils
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}


/**
  * Created by alexsilva on 5/17/17.
  */
class KafkaUtilsSpec extends WordSpec with BeforeAndAfterAll with Matchers with Eventually {

  implicit val config = EmbeddedKafkaConfig(kafkaPort = 8092, zooKeeperPort = 3181)

  override def beforeAll() = {
    super.beforeAll()
    EmbeddedKafka.start()
    EmbeddedKafka.createCustomTopic("test-kafka-utils")
  }

  override def afterAll() = {
    super.afterAll()
    KafkaUtils.zkUtils.foreach(_.close())
    EmbeddedKafka.stop()
  }

  "Kafka Utils" should {
    "return false for a topic that doesn't exist" in {
      assert(!KafkaUtils.topicExists("test_123123"))
    }

    "return true for a topic that exists" in {
      assert(KafkaUtils.topicExists("test-kafka-utils"))
    }

    "return a list of topics" in {
      KafkaUtils.topicNames().get.indexOf("test-kafka-utils") should be > -1
    }

    "loads default consumer" in {
      val d = KafkaUtils.defaultConsumerSettings("avro")
      val props = Map("key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "auto.offset.reset" -> "latest", "group.id" -> "hydra",
        "bootstrap.servers" -> "localhost:8092",
        "enable.auto.commit" -> "false",
        "value.deserializer" -> "io.confluent.kafka.serializers.KafkaAvroDeserializer",
        "zookeeper.connect" -> "localhost:3181", "client.id" -> "hydra.avro", "metadata.fetch.timeout.ms" -> "100000",
        "value.serializer" -> "io.confluent.kafka.serializers.KafkaAvroSerializer",
        "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "schema.registry.url" -> "mock")

      d.properties shouldBe props
    }

    "has settings for consumers by format" in {
      val d = KafkaUtils.loadConsumerSettings("string", "hydrag")
      val props = Map("key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "auto.offset.reset" -> "latest", "group.id" -> "hydrag",
        "bootstrap.servers" -> "localhost:8092",
        "enable.auto.commit" -> "false",
        "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "zookeeper.connect" -> "localhost:3181", "client.id" -> "hydra.string", "metadata.fetch.timeout.ms" -> "100000",
        "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "schema.registry.url" -> "mock")

      d.properties shouldBe props
    }
  }
}

