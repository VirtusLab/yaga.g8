package $package$.child

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import yaga.k8sservice.ExtractEndpoints
import sttp.model.StatusCode

case class GreetingResponse(message: String)

object GreetingResponse:
  given Encoder[GreetingResponse] = deriveEncoder[GreetingResponse]
  given Decoder[GreetingResponse] = deriveDecoder[GreetingResponse]

object ChildEndpoints derives ExtractEndpoints:

  val greet: PublicEndpoint[String, Unit, GreetingResponse, Any] =
    endpoint.get
      .in("greet" / path[String]("name"))
      .out(jsonBody[GreetingResponse])
      .errorOut(statusCode(StatusCode.NotFound))

  val health: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get
      .in("health")
      .out(stringBody)
