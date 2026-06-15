package $package$.parent

import io.circe.Codec as CirceCodec
import io.circe.generic.semiauto.*

case class WelcomeResponse(welcome: String)

object WelcomeResponse:
  given CirceCodec[WelcomeResponse] = deriveCodec
