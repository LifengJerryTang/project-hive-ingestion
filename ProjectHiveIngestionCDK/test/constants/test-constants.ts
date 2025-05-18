import { App, Duration, RemovalPolicy, Stack } from 'aws-cdk-lib';
import * as events from 'aws-cdk-lib/aws-events';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import { EventBridgeSchedulerStackProps } from '../../lib/eventbridge/eventbridge-scheduler-stack';

// Dummy CDK App used to bind constructs like Secret and EventBus
// export const testApp = new App();
// export const testStack = new Stack(testApp, 'TestStack1');
// export const testStack2 = new Stack(testApp, 'TestStack2');

export const fifteenMinutesInSeconds = Duration.minutes(15).toSeconds();
export const defaultEventBusName = 'ProjectHiveIngestionEventBus';
export const customEventBusName = 'CustomBusName';

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