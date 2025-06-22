// lib/ingestions/gmail-ingestion-stack.ts

import { RemovalPolicy, Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import { FIFTEEN_DAYS_DURATION, FIFTEEN_MINUTES_DURATION } from '../constants';
import { ITable } from 'aws-cdk-lib/aws-dynamodb';

interface GmailIngestionStackProps extends StackProps {
  readonly eventBus: events.IEventBus;
  readonly gmailOAuthSecret: secretsmanager.ISecret;
  readonly removalPolicy: RemovalPolicy;
  readonly messagesTable: ITable;
}

export class GmailIngestionStack extends Stack {
  constructor(scope: Construct, id: string, props: GmailIngestionStackProps) {
    super(scope, id, props);

    const gmailIngestionLambda = new lambda.Function(this, 'GmailIngestionlambda', {
      runtime: lambda.Runtime.JAVA_21,
      handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
      code: lambda.Code.fromAsset('../ProjectHiveIngestionService/build/libs/ProjectHiveIngestionService-1.0-SNAPSHOT-all.jar'),
      memorySize: 1024,
      timeout: FIFTEEN_MINUTES_DURATION,
    });

    props.gmailOAuthSecret.grantRead(gmailIngestionLambda);
    props.eventBus.grantPutEventsTo(gmailIngestionLambda);
    props.messagesTable.grantReadWriteData(gmailIngestionLambda);

    const rule = new events.Rule(this, 'GmailIngestionScheduleRule', {
      schedule: events.Schedule.rate(FIFTEEN_DAYS_DURATION),
      targets: [new targets.LambdaFunction(gmailIngestionLambda)],
    });

    rule.applyRemovalPolicy(props.removalPolicy);
  }
}
