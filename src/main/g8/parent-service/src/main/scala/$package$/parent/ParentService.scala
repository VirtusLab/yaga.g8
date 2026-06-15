package $package$.parent

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.client.sttp4.SttpClientInterpreter
import sttp.client4.*
import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}

import besom.json.*
import yaga.k8sservice.NettySyncServerApp
import yaga.k8sservice.OpenApiServiceReference

import $package$.child.{ChildEndpoints, GreetingResponse}

case class WelcomeResponse(welcome: String)

object WelcomeResponse:
  given Encoder[WelcomeResponse] = deriveEncoder[WelcomeResponse]
  given Decoder[WelcomeResponse] = deriveDecoder[WelcomeResponse]

case class ServerConfig(
    childService: OpenApiServiceReference[ChildEndpoints.type]
) derives JsonReader

object ParentService extends NettySyncServerApp[ServerConfig]:

  def serviceName: String = "parent-service"
  def serviceVersion: String = "0.1.0-SNAPSHOT"

  override def serverEndpoints(config: ServerConfig): List[ServerEndpoint] =
    lazy val backend: SyncBackend = DefaultSyncBackend()
    given SttpClientInterpreter = SttpClientInterpreter()

    lazy val childClient = config.childService.toRequestThrowErrors

    val welcomeEndpoint: PublicEndpoint[String, Unit, WelcomeResponse, Any] =
      endpoint.get
        .in("welcome" / path[String]("name"))
        .out(jsonBody[WelcomeResponse])

    val welcomeServerEndpoint = welcomeEndpoint.handleSuccess { name =>
      val greeting = childClient.greet(name).send(backend).body
      WelcomeResponse(welcome = s"Welcome! \${greeting.message}")
    }

    List(welcomeServerEndpoint)
