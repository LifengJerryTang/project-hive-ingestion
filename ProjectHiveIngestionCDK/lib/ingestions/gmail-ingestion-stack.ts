import { RemovalPolicy, Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as path from 'path';
import { FIFTEEN_MINUTES_DURATION } from '../constants';

interface GmailIngestionStackProps extends StackProps {
  readonly eventBus: events.IEventBus;
  readonly removalPolicy?: RemovalPolicy;
}

export class GmailIngestionStack extends Stack {
  constructor(scope: Construct, id: string, props: GmailIngestionStackProps) {
    super(scope, id, props);

    const gmailIngestionLambda = new lambda.Function(this, 'GmailIngestionlambda', {
        runtime: lambda.Runtime.JAVA_21,
        handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
        code: lambda.Code.fromAsset(
            path.join(__dirname, '../../artifacts/gmail-ingestion-lambda.jar')
        ),
        memorySize: 1024,
        timeout: FIFTEEN_MINUTES_DURATION
    });

    props.eventBus.grantPutEventsTo(gmailIngestionLambda);

    const rule = new events.Rule(this, 'GmailIngestionScheduleRule', {
        schedule: events.Schedule.rate(FIFTEEN_MINUTES_DURATION),
        targets: [new targets.LambdaFunction(gmailIngestionLambda)]
    });

    rule.applyRemovalPolicy(props.removalPolicy ?? RemovalPolicy.DESTROY);
 
  }
}