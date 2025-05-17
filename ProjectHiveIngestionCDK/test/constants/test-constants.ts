import { App, Duration, RemovalPolicy } from 'aws-cdk-lib';
import * as events from 'aws-cdk-lib/aws-events';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import { EventBridgeSchedulerStackProps } from '../../lib/eventbridge/eventbridge-scheduler-stack';

// Dummy CDK App used to bind constructs like Secret and EventBus
export const testApp = new App();

export const fifteenMinutesInSeconds = Duration.minutes(15).toSeconds();
export const defaultEventBusName = 'ProjectHiveIngestionEventBus';
export const customEventBusName = 'CustomBusName';

export const mockEventBus = events.EventBus.fromEventBusArn(
  testApp,
  'MockEventBus',
  'arn:aws:events:us-west-2:123456789012:event-bus/ProjectHiveIngestionEventBus'
);

export const mockGmailOAuthSecret = secretsmanager.Secret.fromSecretNameV2(
  testApp,
  'MockGmailOAuthSecret',
  'gmail-oauth-secret'
);

export const defaultLambdaProps = {
  eventBus: mockEventBus,
  gmailOAuthSecret: mockGmailOAuthSecret,
  removalPolicy: RemovalPolicy.DESTROY
};

export const defaultSchedulerProps: EventBridgeSchedulerStackProps = {
  eventBusName: defaultEventBusName,
  removalPolicy: RemovalPolicy.DESTROY
};

export const customSchedulerProps: EventBridgeSchedulerStackProps = {
  eventBusName: customEventBusName,
  removalPolicy: RemovalPolicy.DESTROY
};

export const defaultEventBridgeProps = {
  removalPolicy: RemovalPolicy.DESTROY,
  eventBusName: 'ProjectHiveIngestionEventBus'
};