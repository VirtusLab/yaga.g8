# Yaga Giter8 Templates

[Giter8](http://www.foundweekends.org/giter8/) templates for [Yaga](https://github.com/VirtusLab/yaga) projects.

Each template lives on its own branch. Use `--branch` to select one.

## Available templates

### AWS Lambda (ScalaJS)

Two lambdas (parent calling child) with Besom infrastructure.

```
sbt new VirtusLab/yaga.g8 --branch aws-lambda-js
```

### Kubernetes JVM Services

Two HTTP services (parent calling child) with Besom infrastructure.

```
sbt new VirtusLab/yaga.g8 --branch k8s-jvm
```

### Kubernetes WASM Service (coming soon)

```
sbt new VirtusLab/yaga.g8 --branch k8s-wasm
```
