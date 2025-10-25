import { Stack, StackProps, Duration } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Function, Runtime, Code, StartingPosition } from 'aws-cdk-lib/aws-lambda';
import { ITable } from 'aws-cdk-lib/aws-dynamodb';
import { PolicyStatement } from 'aws-cdk-lib/aws-iam';
import { DynamoEventSource } from 'aws-cdk-lib/aws-lambda-event-sources';
import * as lambda from 'aws-cdk-lib/aws-lambda';

export interface SummarizationStackProps extends StackProps {
  readonly messageTable: ITable;
  readonly summaryTable: ITable;
}

export class SummarizationStack extends Stack {
  constructor(scope: Construct, id: string, props: SummarizationStackProps) {
    super(scope, id, props);

    const summarizationLambda = new Function(this, 'SummarizationLambda', {
      runtime: Runtime.JAVA_21,
      handler: 'com.projecthive.summarization.SummarizationHandler::handleRequest',
      code: lambda.Code.fromAsset('../ProjectHiveSummarizationService/build/libs/ProjectHiveSummarizationService-1.0-SNAPSHOT-all.jar'),
      memorySize: 1024,
      timeout: Duration.seconds(30),
      environment: {
        SUMMARY_TABLE_NAME: props.summaryTable.tableName,
        MESSAGE_TABLE_NAME: props.messageTable.tableName,
      },
    });

    // Grant read access to the DynamoDB stream
    summarizationLambda.addToRolePolicy(new PolicyStatement({
      actions: [
        'dynamodb:DescribeStream',
        'dynamodb:GetRecords',
        'dynamodb:GetShardIterator',
        'dynamodb:ListStreams',
      ],
      resources: [props.messageTable.tableStreamArn!],
    }));

    // Grant write access to the summary table
    summarizationLambda.addToRolePolicy(new PolicyStatement({
      actions: ['dynamodb:PutItem'],
      resources: [props.summaryTable.tableArn],
    }));

    // Grant permission to invoke Claude 3 Sonnet on Bedrock
    summarizationLambda.addToRolePolicy(new PolicyStatement({
      actions: ['bedrock:InvokeModel'],
      resources: [
        'arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0',
      ],
    }));

    // Add the DynamoDB Stream as a trigger
    summarizationLambda.addEventSource(new DynamoEventSource(props.messageTable, {
      startingPosition: StartingPosition.LATEST,
      batchSize: 5,
      retryAttempts: 2,
    }));
  }
}
