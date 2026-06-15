# $name$

Kubernetes WASM microservices built with [Yaga](https://github.com/VirtusLab/yaga), [Tapir](https://tapir.softwaremill.com/), and [Besom](https://github.com/VirtusLab/besom) (Pulumi Scala SDK).

Services are compiled to WebAssembly via the [scala-wasm](https://github.com/nicklausw/scala-wasm) fork and deployed to Kubernetes using the WASI HTTP component model.

## Project structure

| Module | Description |
|--------|-------------|
| `child-endpoints` | Shared Tapir endpoint definitions (`GET /greet/{name}`, `GET /health`) |
| `child-service` | WASM service implementing the child endpoints |
| `parent-service` | WASM service that calls child-service (`GET /welcome/{name}`) |
| `infra` | Besom/Pulumi infrastructure: k8s namespace, Docker images, Deployments, Services |

## Prerequisites

- JDK 21+ and sbt
- [wasmtime](https://wasmtime.dev) v43+ (for local testing)
- [pulumi](https://www.pulumi.com/docs/install/) and [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Docker (for building WASM container images)
- Rust toolchain (for building the patched runwasi shim)

## Getting started

### 1. Local run (no cluster needed)

Build the WASM binaries and test locally with `wasmtime serve`:

```bash
# Build WASM binaries
just wasm-build

# Terminal 1: start child-service
just run-child

# Terminal 2: start parent-service (calls child on port 8080)
just run-parent

# Terminal 3: test
curl http://127.0.0.1:8080/greet/World
# => {"message":"Hello, World!"}

curl http://127.0.0.1:8081/welcome/World
# => {"welcome":"Welcome! Hello, World!"}
```

### 2. Cluster deployment

#### Setting up the wasmtime RuntimeClass

Kubernetes needs a custom container runtime to execute WASM workloads. This project
uses the **RuntimeClass** deployment mode where WASM binaries run directly on the
node via a patched `containerd-shim-wasmtime-v1` shim.

The upstream [runwasi](https://github.com/containerd/runwasi) shim does not work
with Scala-WASM because it uses wasmtime 36.x and lacks support for the `gc`,
`function-references`, and `exceptions` WASM features that Scala-WASM requires.

**Step 1: Add the patched runwasi fork**

```bash
git submodule add https://github.com/lbialy/runwasi vendor/runwasi
git submodule update --init --recursive
```

This fork ([lbialy/runwasi](https://github.com/lbialy/runwasi)) upgrades wasmtime
to v43 and enables the required experimental features.

**Step 2: Build the shim**

```bash
just build-shim
```

This produces `vendor/runwasi/target/release/containerd-shim-wasmtime-v1`.

**Step 3: Install the shim on your k8s node(s)**

Copy the binary to each node that will run WASM workloads:

```bash
scp vendor/runwasi/target/release/containerd-shim-wasmtime-v1 <node>:/usr/local/bin/
```

Then configure containerd on each node to register the wasmtime runtime. Add to
`/etc/containerd/config.toml`:

```toml
[plugins.'io.containerd.cri.v1.runtime'.containerd.runtimes.wasmtime]
  runtime_type = '/usr/local/bin/containerd-shim-wasmtime-v1'
  [plugins.'io.containerd.cri.v1.runtime'.containerd.runtimes.wasmtime.options]
```

Restart containerd after editing the config.

**Step 4: Create the RuntimeClass**

```bash
just create-runtime-class
```

#### Deploy with Pulumi

Configure the Pulumi stack:

```bash
pulumi stack init dev
pulumi config set registryName <your-registry>
pulumi config set registryUsername <username>
pulumi config set --secret registryPassword <password>
```

Deploy:

```bash
just infra-up
```

## Known caveats

- **No cross-request state.** `wasmtime serve` instantiates a fresh WASM component
  per HTTP request. Any mutable state is reset between calls.
- **Use `fastLinkJS`, not `fullLinkJS`.** The scala-wasm optimizer crashes on
  tapir/circe-derived code. `fastLinkJS` produces valid WASM binaries.
- **`imageSecrets` is required.** The generated service args always require a
  Docker registry secret, even for unauthenticated registries.
