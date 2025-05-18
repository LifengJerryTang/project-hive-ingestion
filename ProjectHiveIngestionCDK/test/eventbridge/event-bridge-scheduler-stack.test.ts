import { Template } from 'aws-cdk-lib/assertions';
import { EventBridgeSchedulerStack } from '../../lib/eventbridge/eventbridge-scheduler-stack';
import {
  customSchedulerProps,
  defaultSchedulerProps,
  defaultEventBusName,
  customEventBusName
} from '../constants/test-constants';
import { App, Stack } from 'aws-cdk-lib';

describe('EventBridgeSchedulerStack', () => {
  test('creates an EventBridge successful', () => {
    const stack = new EventBridgeSchedulerStack(new App(), 'TestStackCustom', customSchedulerProps);
    const template = Template.fromStack(stack);
    
    template.hasResourceProperties('AWS::Events::EventBus', {
      Name: customEventBusName
    });
  });

  test('eventBus instance is accessible and has correct name', () => {
    const stack = new EventBridgeSchedulerStack(new Stack(), 'TestStackAccess', defaultSchedulerProps);
    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::Events::EventBus', {
      Name: defaultEventBusName
    });
  });
});
