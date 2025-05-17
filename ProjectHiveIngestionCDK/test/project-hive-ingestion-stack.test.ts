import { Template, Match } from 'aws-cdk-lib/assertions';
import { ProjectHiveIngestionStack } from '../lib/project-hive-ingestion-stack';
import { testApp, defaultLambdaProps, defaultEventBridgeProps } from './constants/test-constants';

describe('ProjectHiveIngestionStack', () => {
  const stack = new ProjectHiveIngestionStack(testApp, 'ProjectHiveRootStack');

  const template = Template.fromStack(stack);

  test('includes an EventBridge bus with correct name', () => {
    template.hasResourceProperties('AWS::Events::EventBus', {
      Name: defaultEventBridgeProps.eventBusName
    });
  });

  test('includes a Secrets Manager secret with correct name and description', () => {
    template.hasResourceProperties('AWS::SecretsManager::Secret', {
      Name: 'gmail-oauth-token',
      Description: 'Stores Gmail access and refresh tokens for Project Hive'
    });
  });

  test('includes a Lambda function for Gmail ingestion with correct handler and runtime', () => {
    template.hasResourceProperties('AWS::Lambda::Function', {
      Handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
      Runtime: 'java21'
    });
  });

  test('includes an EventBridge rule that schedules every 15 minutes', () => {
    template.hasResourceProperties('AWS::Events::Rule', {
      ScheduleExpression: 'rate(15 minutes)'
    });
  });

  test('includes IAM permissions for SecretsManager and EventBridge', () => {
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
