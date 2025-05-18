import { App, Stack, RemovalPolicy } from 'aws-cdk-lib';
import { Template, Match } from 'aws-cdk-lib/assertions';
import { GmailIngestionStack } from '../../lib/ingestions/gmail-ingestion-stack';
import * as events from 'aws-cdk-lib/aws-events';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';

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

  return { mockEventBus, mockGmailOAuthSecret };
}

describe('GmailIngestionStack', () => {
  test('creates Gmail ingestion lambda with correct configuration', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'LambdaConfigTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      removalPolicy: RemovalPolicy.DESTROY
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::Lambda::Function', Match.objectLike({
      Properties: Match.objectLike({
        Handler: 'index.handler',
        Runtime: 'nodejs18.x',
        MemorySize: 1024,
        Timeout: Match.anyValue()
      })
    }));
  });

  test('creates EventBridge rule with 15-minute schedule targeting the lambda', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'ScheduleRuleTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
      removalPolicy: RemovalPolicy.RETAIN
    });

    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::Events::Rule', {
      ScheduleExpression: 'rate(15 minutes)'
    });
  });

  test('grants permission for secret read and event bus put', () => {
    const app = new App();
    const { mockEventBus, mockGmailOAuthSecret } = createMocks(app);

    const stack = new GmailIngestionStack(app, 'PermissionTestStack', {
      eventBus: mockEventBus,
      gmailOAuthSecret: mockGmailOAuthSecret,
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
            }),
            Match.objectLike({
              Action: 'events:PutEvents'
            })
          ])
        })
      })
    }));
  });
});
