import sbtcrossproject.CrossPlugin.autoImport.*
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.*
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.*
import org.scalajs.linker.interface.ModuleKind
import yaga.sbt.k8sservice.WasmRuntime

ThisBuild / resolvers += "Sonatype Central Snapshots" at "https://central.sonatype.com/repository/maven-snapshots/"

val tapirVersion = "1.13.5-WASM-1"
val circeVersion = "0.14.15-WASM-1"
val yagaVersion = "$yaga_version$"

val wasmRuntime: WasmRuntime = sys.env.getOrElse("WASM_RUNTIME", "runtimeclass").toLowerCase match {
  case "embedded" => WasmRuntime.EmbeddedWasmtime
  case _          => WasmRuntime.RuntimeClass("wasmtime")
}

// Ground rule: the yaga AutoPlugin does not set scalaOrganization / scalaVersion /
// scalaCompilerBridgeBinaryJar for the JS halves of WASM services. Every WASM
// service project must apply these settings to JS platform subprojects itself.
lazy val jsPlatformSettings: Seq[Setting[_]] = Seq(
  scalaOrganization := "io.github.scala-wasm",
  scalaVersion := "3.8.3-RC1-wasm.4",

  scalaCompilerBridgeBinaryJar := {
    val sv = scalaVersion.value
    val bridgeModule = "io.github.scala-wasm" % "scala3-sbt-bridge" % sv
    val descriptor = dependencyResolution.value.wrapDependencyInModule(bridgeModule)
    val jar = dependencyResolution.value
      .update(
        descriptor,
        updateConfiguration.value,
        (update / unresolvedWarningConfiguration).value,
        streams.value.log
      )
      .toOption
      .flatMap { report =>
        report
          .select(
            configurationFilter(Compile.name),
            moduleFilter(bridgeModule.organization, bridgeModule.name, bridgeModule.revision),
            artifactFilter(extension = "jar", classifier = "")
          )
          .headOption
      }
    Some(jar.getOrElse(sys.error(s"Could not resolve \$bridgeModule")))
  }
)

lazy val `child-endpoints` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("child-endpoints"))
  .settings(scalaVersion := "$scala_version$")
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.github.florian3k.sttp.tapir" %% "tapir-core" % tapirVersion,
      "io.github.florian3k.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "io.github.florian3k.circe" %% "circe-core" % circeVersion,
      "io.github.florian3k.circe" %% "circe-generic" % circeVersion,
      "org.virtuslab" %% "yaga-wasm-service-sdk" % yagaVersion
    )
  )
  .jsSettings(jsPlatformSettings*)
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.github.florian3k.sttp.tapir" %%% "tapir-core" % tapirVersion,
      "io.github.florian3k.sttp.tapir" %%% "tapir-json-circe" % tapirVersion,
      "io.github.florian3k.circe" %%% "circe-core" % circeVersion,
      "io.github.florian3k.circe" %%% "circe-generic" % circeVersion,
      "org.virtuslab" %%% "yaga-wasm-service-sdk-runtime" % yagaVersion
    )
  )

lazy val `child-service` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("child-service"))
  .dependsOn(`child-endpoints`)
  .yagaWasmService
  .settings(scalaVersion := "$scala_version$")
  .jsSettings(jsPlatformSettings*)

lazy val `parent-service` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("parent-service"))
  .dependsOn(`child-endpoints`)
  .yagaWasmService
  .yagaWasmServiceClient
  .settings(scalaVersion := "$scala_version$")
  .jsSettings(jsPlatformSettings*)

lazy val infra = project
  .in(file("infra"))
  .settings(
    scalaVersion := "$scala_version$",
    libraryDependencies ++= Seq(
      "org.virtuslab" %% "besom-kubernetes" % "4.22.1-core.0.5",
      "org.virtuslab" %% "besom-docker" % "4.6.2-core.0.5"
    )
  )
  .withYagaDependencies(
    `child-service`.yagaWasmServiceInfra(wasmRuntime = wasmRuntime),
    `parent-service`.yagaWasmServiceInfra(wasmRuntime = wasmRuntime)
  )
