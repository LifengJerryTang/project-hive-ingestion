import { RemovalPolicy, Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as path from 'path';
import { FIFTEEN_MINUTES_DURATION } from '../constants';

interface GmailIngestionStackProps extends StackProps {
  readonly eventBus: events.IEventBus;
  readonly gmailOAuthSecret: secretsmanager.ISecret
  readonly removalPolicy: RemovalPolicy;
}

export class GmailIngestionStack extends Stack {
  constructor(scope: Construct, id: string, props: GmailIngestionStackProps) {
    super(scope, id, props);

    // Create a Lambda function for ingesting Gmail emails
    const gmailIngestionLambda = new lambda.Function(this, 'GmailIngestionlambda', {
        runtime: lambda.Runtime.JAVA_21,
        handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
        code: lambda.Code.fromAsset(
            path.join(__dirname, '../../artifacts/gmail-ingestion-lambda.jar')
        ),
        memorySize: 1024,
        timeout: FIFTEEN_MINUTES_DURATION
    });

    // Allow the lambda to access the Gmail OAuth secret
    props.gmailOAuthSecret.grantRead(gmailIngestionLambda);

    // Allow the event bus to trigger the lambda
    props.eventBus.grantPutEventsTo(gmailIngestionLambda);

    // Create an EventBridge rule to trigger the lambda every 15 minutes
    const rule = new events.Rule(this, 'GmailIngestionScheduleRule', {
        schedule: events.Schedule.rate(FIFTEEN_MINUTES_DURATION),
        targets: [new targets.LambdaFunction(gmailIngestionLambda)]
    });

    // Apply removal policy
    rule.applyRemovalPolicy(props.removalPolicy ?? RemovalPolicy.DESTROY);
 
  }
}