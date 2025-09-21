import { App, Stack, RemovalPolicy } from 'aws-cdk-lib';
import { Template, Match } from 'aws-cdk-lib/assertions';
import { GmailIngestionStack } from '../../lib/ingestions/gmail-ingestion-stack';
import * as events from 'aws-cdk-lib/aws-events';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import { Runtime } from 'aws-cdk-lib/aws-lambda';

function createMocks(app: App) {
  const contextStack = new Stack(app, 'ContextStack');

  const mockEventBus = events.EventBus.fromEventBusArn(
    contextStack,
    'MockEventBus',
    'arn:aws:events:us-west-2:123456789012:event-bus/ProjectHiveIngestionEventBus'
  );

  const mockGmailOAuthSecret = secretsmanager.Secret.fromSecretNameV2(
    contextStack,
    'MockGmailOAuthSecret',
    'gmail-oauth-secret'
  );

  const mockMessagesTable = dynamodb.Table.fromTableArn(
    contextStack,
    'MockMessagesTable',
    'arn:aws:dynamodb:us-west-2:123456789012:table/messages'
  );

  return { mockEventBus, mockGmailOAuthSecret, mockMessagesTable };
}

describe('GmailIngestionStack', () => {
  test('creates Gmail ingestion lambda with correct configuration', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret, mockMessagesTable } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'LambdaConfigTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      messagesTable: mockMessagesTable,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::Lambda::Function', Match.objectLike({
      Properties: Match.objectLike({
        Handler: 'com.projecthive.ingestion.handlers.GmailIngestionHandler::handleRequest',
        Runtime: 'java21',
        MemorySize: 1024,
        Timeout: Match.anyValue()
      })
    }));
  });

  test('creates EventBridge rule with 15-month schedule targeting the lambda', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret, mockMessagesTable } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'ScheduleRuleTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      messagesTable: mockMessagesTable,
      removalPolicy: RemovalPolicy.RETAIN
    });

    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::Events::Rule', {
      ScheduleExpression: 'rate(450 days)'
    });
  });

  test('grants permission for secret read', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret, mockMessagesTable } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'SecretReadTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      messagesTable: mockMessagesTable,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: Match.arrayWith([
                'secretsmanager:GetSecretValue',
                'secretsmanager:DescribeSecret'
              ])
            })
          ])
        })
      })
    }));
  });

  test('grants permission to put events to EventBridge bus', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret, mockMessagesTable } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'EventBusPutTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      messagesTable: mockMessagesTable,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: 'events:PutEvents'
            })
          ])
        })
      })
    }));
  });

  test('grants permission for DynamoDB access to messages table', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret, mockMessagesTable } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'DynamoDbAccessTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      messagesTable: mockMessagesTable,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const template = Template.fromStack(stack);

    // Match just one known DynamoDB action to confirm grant is applied
    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: Match.arrayWith([
                Match.stringLikeRegexp('dynamodb:PutItem')
              ])
            })
          ])
        })
      })
    }));
  });
});
