ThisBuild / scalaVersion := "$scala_version$"
ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val `child-endpoints` = project.in(file("child-endpoints"))
  .yagaOpenApiEndpoints
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.11.41"
    )
  )

lazy val `child-service` = project.in(file("child-service"))
  .yagaOpenApiK8sService(ServerType.NettySync)
  .dependsOn(`child-endpoints`)
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.4.11"
    ),
    dockerBaseImage := "eclipse-temurin:21"
  )

lazy val `parent-service` = project.in(file("parent-service"))
  .yagaOpenApiK8sService(ServerType.NettySync)
  .yagaOpenApiClient
  .dependsOn(`child-endpoints`)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.11.41",
      "ch.qos.logback" % "logback-classic" % "1.4.11"
    ),
    dockerBaseImage := "eclipse-temurin:21"
  )

lazy val infra = project.in(file("infra"))
  .settings(
    libraryDependencies ++= Seq(
      "org.virtuslab" %% "besom-kubernetes" % "4.22.1-core.0.5",
      "org.virtuslab" %% "besom-docker" % "4.6.2-core.0.5"
    )
  )
  .withYagaDependencies(
    `child-service`.yagaK8sServiceInfra(),
    `parent-service`.yagaK8sServiceInfra()
  )
