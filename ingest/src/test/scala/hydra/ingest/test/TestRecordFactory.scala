package hydra.ingest.test

import hydra.core.ingest.HydraRequest
import hydra.core.transport.{HydraRecord, RecordFactory}

import scala.util.Success

object TestRecordFactory extends RecordFactory[String, String] {
  override def build(r: HydraRequest) = {
    val timeout = r.metadataValueEquals("timeout", "true")
    if (timeout) {
      Success(TimeoutRecord("test-topic", Some(r.correlationId.toString), r.payload))
    }
    else {
      Success(TestRecord("test-topic", Some(r.correlationId.toString), r.payload))
    }
  }
}

case class TestRecord(destination: String,
                      key: Option[String],
                      payload: String) extends HydraRecord[String, String]


case class TimeoutRecord(destination: String,
                         key: Option[String],
                         payload: String) extends HydraRecord[String, String]