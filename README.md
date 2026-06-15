# yaga.g8

[Giter8](http://www.foundweekends.org/giter8/) templates for [Yaga](https://github.com/VirtusLab/yaga) projects.

## Available templates

### AWS Lambda (Scala.js)

```bash
sbt new VirtusLab/yaga.g8 --branch aws-lambda-js
```

### Kubernetes JVM services

```bash
sbt new VirtusLab/yaga.g8 --branch k8s-jvm
```

### Kubernetes WASM services

```bash
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
