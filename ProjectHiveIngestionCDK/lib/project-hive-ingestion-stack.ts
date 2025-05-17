import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { EventBridgeSchedulerStack } from './eventbridge/eventbridge-scheduler-stack';
import { OAuthStack } from './oauth/oauth-stack';
import { GmailIngestionStack } from './ingestions/gmail-ingestion-stack';

export class ProjectHiveIngestionStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Step 1: Create the shared EventBridge bus
    const eventBridgeSchedulerStack = new EventBridgeSchedulerStack(this, 'EventBridgeSchedulerStack', {
      eventBusName: 'ProjectHiveIngestionEventBus',
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    // Step 2: Create the OAuth secret stack
    const oauthStack = new OAuthStack(this, 'OAuthStack', {});

    // Step 3: Gmail Ingestion Lambda Stack
    const gmailIngestionStack = new GmailIngestionStack(this, 'GmailIngestionStack', {
      eventBus: eventBridgeSchedulerStack.eventBus,
      gmailOAuthSecret: oauthStack.gmailOAuthSecret,
      removalPolicy: cdk.RemovalPolicy.DESTROY
    })
  }
}
