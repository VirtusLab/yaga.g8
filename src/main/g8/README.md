# $name$

AWS Lambda service built with [Yaga](https://github.com/VirtusLab/yaga).

## Structure

- `child-lambda/` - a lambda handler that processes input and returns output
- `parent-lambda/` - a lambda that invokes the child lambda
- `infra/` - Besom (Pulumi Scala SDK) infrastructure code

## Deployment

Requires [Pulumi](https://www.pulumi.com/docs/install/) and [just](https://github.com/casey/just).

```
just infra-preview
just infra-up
just infra-down
```

## Testing lambdas

Example parent lambda input:

```json
{
  "message": "hello world"
}
```
