package $package$.parent

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

object ParentEndpoints:
  val welcome: PublicEndpoint[String, String, WelcomeResponse, Any] =
    endpoint.get
      .in("welcome" / path[String]("name"))
      .errorOut(stringBody)
      .out(jsonBody[WelcomeResponse])
