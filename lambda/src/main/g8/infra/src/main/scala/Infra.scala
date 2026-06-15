import besom.*
import besom.api.aws
import besom.api.aws.lambda.FunctionArgs
import besom.json.*

import child.$package$.child_lambda.ChildLambda
import parent.$package$.parent_lambda.{ParentLambda, Config as ParentLambdaConfig}

@main def main = Pulumi.run {
  val lambdaRole = aws.iam.Role(
    name = "lambdaRole",
    aws.iam.RoleArgs(
      assumeRolePolicy = json"""{
          "Version": "2012-10-17",
          "Statement": [{
              "Effect": "Allow",
              "Principal": {
                  "Service": "lambda.amazonaws.com"
              },
              "Action": "sts:AssumeRole"
          }]
      }""".map(_.prettyPrint),
      managedPolicyArns = List(aws.iam.enums.ManagedPolicy.AWSLambdaBasicExecutionRole.value)
    )
  )

  val childLambda = ChildLambda(
    "childLambda",
    FunctionArgs(
      role = lambdaRole.arn
    )
  )

  val lambdaInvokePolicy = aws.iam.Policy("lambdaInvokePolicy", aws.iam.PolicyArgs(
    name = "lambdaInvokePolicy",
    policy = json"""{
      "Version": "2012-10-17",
      "Statement": [
        {
            "Sid": "Statement1",
            "Effect": "Allow",
            "Action": [
                "lambda:InvokeFunction",
                "lambda:InvokeAsync"
            ],
            "Resource": [
              \${childLambda.arn}
            ]
        }
      ]
    }""".map(_.prettyPrint)
  ))

  val parentLambdaRole = aws.iam.Role("parentLambdaRole", aws.iam.RoleArgs(
    assumeRolePolicy = json"""{
      "Version": "2012-10-17",
      "Statement": [{
          "Effect": "Allow",
          "Principal": {
              "Service": "lambda.amazonaws.com"
          },
          "Action": "sts:AssumeRole"
      }]
    }""".map(_.prettyPrint),
    managedPolicyArns = List(
      aws.iam.enums.ManagedPolicy.AWSLambdaBasicExecutionRole.value,
      lambdaInvokePolicy.arn
    )
  ))

  val parentLambda = ParentLambda(
    "parentLambda",
    FunctionArgs(
      role = parentLambdaRole.arn,
      timeout = 30
    ),
    config =
      for
        child <- childLambda
      yield
        ParentLambdaConfig(
          childLambda = child.lambdaHandle
        )
  )

  Stack()
    .exports(
      child_lambda_arn = childLambda.arn,
      parent_lambda_arn = parentLambda.arn
    )
}
