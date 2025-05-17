import { Template, Match } from 'aws-cdk-lib/assertions';

import { testApp, defaultLambdaProps, mockEventBus, mockGmailOAuthSecret, fifteenMinutesInSeconds } from '../constants/test-constants';
import { RemovalPolicy } from 'aws-cdk-lib';
import { GmailIngestionStack } from '../../lib/ingestions/gmail-ingestion-stack';

describe('GmailIngestionStack', () => {
  test('creates Gmail ingestion lambda with correct configuration', () => {
    const stack = new GmailIngestionStack(testApp, 'TestStack', defaultLambdaProps);
    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::Lambda::Function', {
      Handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
      Runtime: 'java21',
      MemorySize: 1024,
      Timeout: fifteenMinutesInSeconds
    });
  });

  test('creates EventBridge rule with 15-minute schedule targeting the lambda', () => {
    const stack = new GmailIngestionStack(testApp, 'TestStackSchedule', {
      ...defaultLambdaProps,
      removalPolicy: RemovalPolicy.RETAIN
    });
    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::Events::Rule', {
      ScheduleExpression: 'rate(15 minutes)'
    });
  });

  test('grants permission for secret read and event bus put', () => {
    const stack = new GmailIngestionStack(testApp, 'TestStackPermissions', defaultLambdaProps);
    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      PolicyDocument: Match.objectLike({
        Statement: Match.arrayWith([
          Match.objectLike({
            Action: Match.arrayWith(['secretsmanager:GetSecretValue'])
          }),
          Match.objectLike({
            Action: Match.arrayWith(['events:PutEvents'])
          })
        ])
      })
    }));
  });
});
