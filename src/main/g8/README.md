# $name$

A Kubernetes JVM microservices project built with [Yaga](https://github.com/VirtusLab/yaga) and [Besom](https://github.com/VirtusLab/besom).

## Project structure

- `child-endpoints/` — Shared Tapir endpoint definitions
- `child-service/` — HTTP service implementing the child endpoints
- `parent-service/` — HTTP service that calls the child service
- `infra/` — Besom (Pulumi Scala) infrastructure code

## Getting started

### Build

```
sbt compile
```

### Stage Docker images

```
sbt child-service/docker:stage parent-service/docker:stage
```

### Deploy infrastructure

Configure your Pulumi stack:

```
pulumi stack init dev
pulumi config set registryName <your-registry>
pulumi config set registryUsername <username>
pulumi config set --secret registryPassword <password>
```

Then deploy:

```
just infra-up
```
