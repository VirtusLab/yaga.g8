package $package$.child

import io.circe.Codec as CirceCodec
import io.circe.generic.semiauto.*

final case class ChildConfig(greeting: String)

object ChildConfig:
  given CirceCodec[ChildConfig] = deriveCodec
