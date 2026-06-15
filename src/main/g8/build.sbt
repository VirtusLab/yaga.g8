ThisBuild / scalaVersion := "$scala_version$"
ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val childLambda = project.in(file("child-lambda"))
  .awsJsLambda()

lazy val parentLambda = project.in(file("parent-lambda"))
  .awsJsLambda()
  .withYagaDependencies(
    childLambda.awsLambdaModel()
  )

lazy val infra = project.in(file("infra"))
  .withYagaDependencies(
    childLambda.awsLambdaInfra(packagePrefix = "child"),
    parentLambda.awsLambdaInfra(packagePrefix = "parent")
  )
