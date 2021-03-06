package hydra.core.app

import akka.actor.Props
import akka.event.slf4j.SLF4JLogging
import com.github.vonnagy.service.container.http.routing.RoutedEndpoints
import com.github.vonnagy.service.container.listener.ContainerLifecycleListener
import com.github.vonnagy.service.container.service.ContainerService
import com.github.vonnagy.service.container.{ContainerBuilder, MissingConfigException}
import com.typesafe.config.Config
import configs.syntax._
import hydra.common.config.ConfigSupport
import hydra.core.extensions.HydraExtensionListener

/**
  * Created by alexsilva on 2/24/17.
  */
trait HydraEntryPoint extends App with SLF4JLogging with ConfigSupport {

  type ENDPOINT = Class[_ <: RoutedEndpoints]

  def config: Config = rootConfig

  def services: Seq[(String, Props)]

  def containerName: String = s"$applicationName"

  def listeners: Seq[ContainerLifecycleListener] = Seq.empty

  lazy val endpoints = config.get[List[String]](s"$applicationName.endpoints").valueOrElse(Seq.empty)
    .map(Class.forName(_).asInstanceOf[ENDPOINT])

  def buildContainer(): ContainerService = {
    val builder = ContainerBuilder()
      .withConfig(config)
      .withRoutes(endpoints: _*)
      .withActors(services: _*)
      .withListeners(new HydraExtensionListener(applicationConfig) +: listeners: _*)
      .withName(containerName)

    builder.build
  }

  def validateConfig(paths: String*) = {
    paths.foreach { path =>
      if (!config.hasPath(path)) {
        throw new MissingConfigException(s"Missing required config property: '$path'.")
      }
    }
  }

}
