package $package$.child

import io.circe.Codec as CirceCodec
import io.circe.generic.semiauto.*

case class GreetingResponse(message: String)

object GreetingResponse:
  given CirceCodec[GreetingResponse] = deriveCodec
