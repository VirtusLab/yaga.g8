package $package$.child

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import yaga.wasmservice.ExtractEndpoints

object ChildEndpoints derives ExtractEndpoints:

  val greet: PublicEndpoint[String, Unit, GreetingResponse, Any] =
    endpoint.get
      .in("greet" / path[String]("name"))
      .out(jsonBody[GreetingResponse])
      .errorOut(statusCode(sttp.model.StatusCode.NotFound))

  val health: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get
      .in("health")
      .out(stringBody)
