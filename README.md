# Yaga Giter8 Templates

[Giter8](http://www.foundweekends.org/giter8/) templates for [Yaga](https://github.com/VirtusLab/yaga) projects.

Each template lives on its own orphan branch. Use `--branch` to select one.

## Available templates

### AWS Lambda (ScalaJS)

Two lambdas (parent calling child) with Besom infrastructure.

```
sbt new VirtusLab/yaga.g8 --branch aws-lambda-js
```

### Kubernetes JVM Services

Two HTTP services (parent calling child) with shared Tapir endpoint definitions, deployed to Kubernetes as JVM containers via Besom.

```
sbt new VirtusLab/yaga.g8 --branch k8s-jvm
```

### Kubernetes WASM Services

Two WASM microservices (parent calling child) compiled to WebAssembly via scala-wasm, deployed to Kubernetes using the WASI HTTP component model. Includes instructions for the patched runwasi shim.

```
sbt new VirtusLab/yaga.g8 --branch k8s-wasm
```

## Template parameters

All templates accept:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `name` | `my-service` | Project name |
| `organization` | `com.example` | Organization / group ID |
| `scala_version` | `3.3.7` | Scala version (JVM side) |
| `yaga_version` | `0.1.0` | Yaga SDK version |
