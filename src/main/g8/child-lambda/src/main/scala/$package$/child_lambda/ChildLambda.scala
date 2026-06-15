package $package$.child_lambda

import yaga.extensions.aws.lambda.LambdaHandler
import besom.json.*

case class Input(message: String) derives JsonFormat
case class Output(result: String) derives JsonFormat

class ChildLambda extends LambdaHandler[Unit, Input, Output]:
  override def handleInput(event: Input) =
    println(s"Child lambda received: \${event.message}")
    Output(result = event.message.toUpperCase)
