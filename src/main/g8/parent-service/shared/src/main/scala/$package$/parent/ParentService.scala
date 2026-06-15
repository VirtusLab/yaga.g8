package $package$.parent

import sttp.shared.Identity
import sttp.tapir.client.sttp4.SttpClientInterpreter
import yaga.wasmservice.WasmServiceApp

import $package$.child.{ChildEndpoints, GreetingResponse}

object ParentService extends WasmServiceApp[ParentConfig]:
  override def serviceName: String = "parent-service"
  override def serviceVersion: String = "0.1.0"

  override def serverEndpoints(config: ParentConfig): List[Endpoint] =
    val handler = ParentEndpoints.welcome.serverLogic[Identity] { name =>
      given SttpClientInterpreter = SttpClientInterpreter()
      val backend = PlatformBackend.sync
      val childClient = config.childService.toRequestThrowErrors

      try
        val greeting = childClient.greet(name).send(backend).body
        Right(WelcomeResponse(welcome = s"Welcome! \${greeting.message}"))
      catch
        case t: Throwable => Left(s"upstream failure: \${t.getMessage}")
    }

    List(handler)
