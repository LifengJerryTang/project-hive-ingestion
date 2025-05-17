import { Template } from 'aws-cdk-lib/assertions';
import { EventBridgeSchedulerStack } from '../../lib/eventbridge/eventbridge-scheduler-stack';
import {
  testApp,
  customSchedulerProps,
  defaultSchedulerProps,
  defaultEventBusName,
  customEventBusName
} from '../constants/test-constants';

describe('EventBridgeSchedulerStack', () => {
  test('creates an EventBridge successful', () => {
    const stack = new EventBridgeSchedulerStack(testApp, 'TestStackCustom', customSchedulerProps);

    const template = Template.fromStack(stack);
    template.hasResourceProperties('AWS::Events::EventBus', {
      Name: customEventBusName
    });
  });

  test('eventBus instance is accessible', () => {
    const stack = new EventBridgeSchedulerStack(testApp, 'TestStackAccess', defaultSchedulerProps);

    expect(stack.eventBus).toBeDefined();
    expect(stack.eventBus.eventBusName).toBe(defaultEventBusName);
  });
});
