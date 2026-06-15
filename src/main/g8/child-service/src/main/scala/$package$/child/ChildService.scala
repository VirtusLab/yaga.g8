package $package$.child

import sttp.tapir.*
import yaga.k8sservice.NettySyncServerApp
import besom.json.*

case class ServerConfig(
    greeting: String
) derives JsonReader

object ChildService extends NettySyncServerApp[ServerConfig]:

  def serviceName: String = "child-service"
  def serviceVersion: String = "0.1.0-SNAPSHOT"

  override def serverEndpoints(config: ServerConfig): List[ServerEndpoint] =

    val greetServerEndpoint = ChildEndpoints.greet.handleSuccess { name =>
      GreetingResponse(message = s"\${config.greeting}, \${name}!")
    }

    val healthServerEndpoint = ChildEndpoints.health.handleSuccess { _ =>
      "OK"
    }

    List(greetServerEndpoint, healthServerEndpoint)
