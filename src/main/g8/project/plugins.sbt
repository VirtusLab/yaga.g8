resolvers += "Sonatype Central Snapshots" at "https://central.sonatype.com/repository/maven-snapshots/"

addSbtPlugin("io.github.scala-wasm" % "sbt-scalajs" % "1.21.1-wasm.4")

addSbtPlugin(
  ("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
    .exclude("org.scala-js", "sbt-scalajs")
)

addSbtPlugin("org.virtuslab" % "sbt-yaga-k8s-service" % "$yaga_version$")
