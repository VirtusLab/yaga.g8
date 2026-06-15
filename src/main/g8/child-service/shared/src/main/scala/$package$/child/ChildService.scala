package $package$.child

import sttp.shared.Identity
import yaga.wasmservice.WasmServiceApp

object ChildService extends WasmServiceApp[ChildConfig]:
  override def serviceName: String = "child-service"
  override def serviceVersion: String = "0.1.0"

  override def serverEndpoints(config: ChildConfig): List[Endpoint] =
    val greetHandler =
      ChildEndpoints.greet.serverLogicPure[Identity] { name =>
        Right(GreetingResponse(message = s"\${config.greeting}, \${name}!"))
      }

    val healthHandler =
      ChildEndpoints.health.serverLogicPure[Identity] { _ =>
        Right("OK")
      }

    List(greetHandler, healthHandler)
