import { RemovalPolicy, Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as events from 'aws-cdk-lib/aws-events';

export interface EventBridgeSchedulerStackProps extends StackProps {
  readonly eventBusName?: string; // optional override
  readonly removalPolicy?: RemovalPolicy
}

export class EventBridgeSchedulerStack extends Stack {
  public readonly eventBus: events.EventBus;

  constructor(scope: Construct, id: string, props?: EventBridgeSchedulerStackProps) {
    super(scope, id, props);

    // Create a custom EventBridge Event Bus
    this.eventBus = new events.EventBus(this, 'ProjectHiveIngestionEventBus', {
      eventBusName: props?.eventBusName ?? 'ProjectHiveIngestionEventBus',
    });
  }
}