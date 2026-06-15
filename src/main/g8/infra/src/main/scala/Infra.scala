import besom.*
import besom.api.kubernetes
import besom.api.docker

import $package$.child.{ChildService, ChildServiceArgs, ChildConfig}
import $package$.parent.{ParentService, ParentServiceArgs, ParentConfig}

import yaga.wasmservice.{ImageCoordinates, ImagePlatform}

@main def main = Pulumi.run:
  val namespaceName = "$name;format="normalize"$"
  val registryName = config.getString("registryName").getOrFail(Exception("registryName must be set in Pulumi config"))
  val registryUsername = config.getString("registryUsername").getOrFail(Exception("registryUsername must be set in Pulumi config"))
  val registryPassword = config.getString("registryPassword").getOrFail(Exception("registryPassword must be set in Pulumi config"))

  val namespace = kubernetes.core.v1.Namespace(
    namespaceName,
    kubernetes.core.v1.NamespaceArgs(
      metadata = kubernetes.meta.v1.inputs.ObjectMetaArgs(name = namespaceName)
    )
  )

  val dockerConfigJson = for
    reg <- registryName
    user <- registryUsername
    pass <- registryPassword
  yield s"""{"auths":{"\${reg}":{"username":"\${user}","password":"\${pass}"}}}"""

  val dockerSecret = kubernetes.core.v1.Secret(
    "docker-secret",
    kubernetes.core.v1.SecretArgs(
      metadata = kubernetes.meta.v1.inputs.ObjectMetaArgs(
        name = "docker-secret",
        namespace = namespaceName,
        annotations = Map(
          "pulumi.com/patchForce" -> "true"
        )
      ),
      `type` = "kubernetes.io/dockerconfigjson",
      stringData = Map(
        ".dockerconfigjson" -> dockerConfigJson
      )
    )
  )

  val childImage = ChildService.imageResource(
    resourceName = "child-image",
    imageCoordinates = ImageCoordinates(
      registry = registryName,
      name = "child-service",
      tag = "0.1.0"
    ),
    registry = docker.inputs.RegistryArgs(
      username = registryUsername,
      password = registryPassword
    )
  )

  val parentImage = ParentService.imageResource(
    resourceName = "parent-image",
    imageCoordinates = ImageCoordinates(
      registry = registryName,
      name = "parent-service",
      tag = "0.1.0"
    ),
    registry = docker.inputs.RegistryArgs(
      username = registryUsername,
      password = registryPassword
    )
  )

  val childApp = ChildService(
    "child-app",
    ChildServiceArgs(
      namespace = namespaceName,
      image = childImage,
      imageSecrets = dockerSecret,
      runConfig = ChildConfig(greeting = "Hello")
    )
  )

  val parentApp = ParentService(
    "parent-app",
    ParentServiceArgs(
      namespace = namespaceName,
      image = parentImage,
      imageSecrets = dockerSecret,
      runConfig =
        for childRef <- childApp.asServiceRef[$package$.child.ChildEndpoints]
        yield ParentConfig(
          childService = childRef
        )
    )
  )

  Stack(namespace, dockerSecret, childImage, childApp, parentImage, parentApp).exports(
    childServiceName = childApp.flatMap(_.serviceName),
    parentServiceName = parentApp.flatMap(_.serviceName)
  )
