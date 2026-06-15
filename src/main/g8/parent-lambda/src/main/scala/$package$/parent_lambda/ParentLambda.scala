package $package$.parent_lambda

import scala.concurrent.ExecutionContext.Implicits.global
import besom.json.*
import yaga.extensions.aws.lambda.{LambdaClient, LambdaHandle, LambdaAsyncHandler}
import $package$.child_lambda

case class Config(
  childLambda: LambdaHandle[child_lambda.Input, child_lambda.Output]
) derives JsonFormat

case class Input(message: String) derives JsonFormat

class ParentLambda extends LambdaAsyncHandler[Config, Input, String]:
  val lambdaClient = LambdaClient()

  override def handleInput(input: Input) =
    val childInput = child_lambda.Input(message = input.message)
    println("Invoking child lambda")
    for
      result <- lambdaClient.invokeWithResponse(config.childLambda, childInput)
    yield
      println(s"Response from child lambda: \$result")
      s"Processed: \${result.result}"
