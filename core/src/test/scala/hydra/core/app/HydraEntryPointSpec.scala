package hydra.core.app

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.http.scaladsl.server.Route
import akka.testkit.TestKit
import com.github.vonnagy.service.container.MissingConfigException
import com.github.vonnagy.service.container.http.routing.RoutedEndpoints
import com.github.vonnagy.service.container.service.ContainerService
import com.typesafe.config.{Config, ConfigFactory}
import hydra.core.test.DummyActor
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

/**
  * Created by alexsilva on 3/7/17.
  */
class HydraEntryPointSpec extends Matchers with FunSpecLike with BeforeAndAfterAll {

  val conf =
    """
      |  hydra_test{
      |
      |  endpoints = ["hydra.core.app.DummyEndpoint"]
      |
      | extensions {
      |    dummy {
      |      enabled = true
      |    }
      |  }
      |}
    """.stripMargin

  val et = new HydraEntryPoint() {
    override def config: Config = ConfigFactory.parseString(conf)

    override def services: Seq[(String, Props)] = Seq("test" -> Props[DummyActor])
  }

  val container = et.buildContainer()

  override def afterAll = {
    TestKit.shutdownActorSystem(container.system)
    container.shutdown()
  }

  describe("When using the HydraEntryPoint class") {

    it("is properly configured") {
      et.config shouldBe ConfigFactory.parseString(conf)
      et.services shouldBe Seq("test" -> Props[DummyActor])
      et.endpoints shouldBe Seq(classOf[DummyEndpoint])
    }

    it("throws error if config is missing") {
      intercept[MissingConfigException] {
        et.validateConfig("tester")
      }
    }

    it("builds a container") {
      val csvc = new ContainerService(Seq(classOf[DummyEndpoint]), Nil, Seq("test" -> Props[DummyActor]), Nil,
        "hydra_test")(container.system)
      csvc.name shouldBe container.name
      csvc.registeredRoutes shouldBe container.registeredRoutes
      csvc.name shouldBe container.name
    }
  }
}

private class DummyEndpoint(implicit s: ActorSystem, implicit val a: ActorRefFactory) extends RoutedEndpoints {
  override def route: Route = get {
    complete("DONE")
  }
}