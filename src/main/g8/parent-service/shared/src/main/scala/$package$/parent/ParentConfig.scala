package $package$.parent

import io.circe.Codec as CirceCodec
import io.circe.generic.semiauto.*
import yaga.wasmservice.OpenApiServiceReference
import $package$.child.ChildEndpoints

final case class ParentConfig(
    childService: OpenApiServiceReference[ChildEndpoints.type]
)

object ParentConfig:
  given CirceCodec[ParentConfig] = deriveCodec
