import { App, Stack, RemovalPolicy } from 'aws-cdk-lib';
import { Template, Match } from 'aws-cdk-lib/assertions';
import { SummarizationStack } from '../../lib/summarization/summarization-stack';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';

function createMocks(app: App) {
  const contextStack = new Stack(app, 'ContextStack');

  const mockMessageTable = dynamodb.Table.fromTableArn(
    contextStack,
    'MockMessageTable',
    'arn:aws:dynamodb:us-west-2:123456789012:table/messages'
  );

  const mockSummaryTable = dynamodb.Table.fromTableArn(
    contextStack,
    'MockSummaryTable',
    'arn:aws:dynamodb:us-west-2:123456789012:table/summaries'
  );

  // Mock the stream ARN for the message table
  Object.defineProperty(mockMessageTable, 'tableStreamArn', {
    value: 'arn:aws:dynamodb:us-west-2:123456789012:table/messages/stream/2023-01-01T00:00:00.000',
    writable: false
  });

  return { mockMessageTable, mockSummaryTable };
}

describe('SummarizationStack', () => {
  test('creates Lambda function with correct configuration', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'LambdaConfigTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::Lambda::Function', Match.objectLike({
      Properties: Match.objectLike({
        Handler: 'com.projecthive.summarization.SummarizationHandler::handleRequest',
        Runtime: 'java21',
        MemorySize: 1024,
        Timeout: 30,
        Environment: {
          Variables: {
            SUMMARY_TABLE_NAME: Match.anyValue(),
            MESSAGE_TABLE_NAME: Match.anyValue()
          }
        }
      })
    }));
  });

  test('sets correct environment variables from props', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'EnvVarsTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::Lambda::Function', Match.objectLike({
      Properties: Match.objectLike({
        Environment: {
          Variables: {
            SUMMARY_TABLE_NAME: 'summaries',
            MESSAGE_TABLE_NAME: 'messages'
          }
        }
      })
    }));
  });

  test('grants DynamoDB stream read permissions', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'StreamPermissionsTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: [
                'dynamodb:DescribeStream',
                'dynamodb:GetRecords',
                'dynamodb:GetShardIterator',
                'dynamodb:ListStreams'
              ],
              Effect: 'Allow',
              Resource: 'arn:aws:dynamodb:us-west-2:123456789012:table/messages/stream/2023-01-01T00:00:00.000'
            })
          ])
        })
      })
    }));
  });

  test('grants DynamoDB summary table write permissions', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'SummaryTablePermissionsTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: 'dynamodb:PutItem',
              Effect: 'Allow',
              Resource: 'arn:aws:dynamodb:us-west-2:123456789012:table/summaries'
            })
          ])
        })
      })
    }));
  });

  test('grants Bedrock Claude 4.5 Sonnet invocation permissions', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'BedrockPermissionsTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            Match.objectLike({
              Action: 'bedrock:InvokeModel',
              Effect: 'Allow',
              Resource: Match.arrayWith([
                // Inference profile with dynamic region and account
                Match.objectLike({
                  'Fn::Join': Match.arrayWith([
                    '',
                    Match.arrayWith([
                      'arn:aws:bedrock:',
                      Match.objectLike({ Ref: 'AWS::Region' }),
                      ':',
                      Match.objectLike({ Ref: 'AWS::AccountId' }),
                      ':inference-profile/global.anthropic.claude-sonnet-4-5-20250929-v1:0'
                    ])
                  ])
                }),
                // Foundation model with dynamic region
                Match.objectLike({
                  'Fn::Join': Match.arrayWith([
                    '',
                    Match.arrayWith([
                      'arn:aws:bedrock:',
                      Match.objectLike({ Ref: 'AWS::Region' }),
                      '::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0'
                    ])
                  ])
                }),
                // Global foundation model
                'arn:aws:bedrock:::foundation-model/anthropic.claude-sonnet-4-5-20250929-v1:0'
              ])
            })
          ])
        })
      })
    }));
  });

  test('configures DynamoDB event source with correct settings', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'EventSourceTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    template.hasResource('AWS::Lambda::EventSourceMapping', Match.objectLike({
      Properties: Match.objectLike({
        EventSourceArn: 'arn:aws:dynamodb:us-west-2:123456789012:table/messages/stream/2023-01-01T00:00:00.000',
        StartingPosition: 'LATEST',
        BatchSize: 5,
        MaximumRetryAttempts: 2
      })
    }));
  });

  test('creates all required IAM policies', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'AllPoliciesTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    // Verify we have at least 3 policy statements (one for each permission set)
    template.hasResource('AWS::IAM::Policy', Match.objectLike({
      Properties: Match.objectLike({
        PolicyDocument: Match.objectLike({
          Statement: Match.arrayWith([
            // DynamoDB stream permissions
            Match.objectLike({
              Action: Match.arrayWith(['dynamodb:DescribeStream'])
            }),
            // Summary table write permissions
            Match.objectLike({
              Action: 'dynamodb:PutItem'
            }),
            // Bedrock permissions
            Match.objectLike({
              Action: 'bedrock:InvokeModel'
            })
          ])
        })
      })
    }));
  });

  test('uses correct Lambda code asset path', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    const stack = new SummarizationStack(app, 'CodeAssetTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable
    });

    const template = Template.fromStack(stack);

    // Verify the Lambda function references the correct code asset
    template.hasResource('AWS::Lambda::Function', Match.objectLike({
      Properties: Match.objectLike({
        Code: Match.objectLike({
          S3Bucket: Match.anyValue(),
          S3Key: Match.anyValue()
        })
      })
    }));
  });

  test('inherits from Stack with correct props', () => {
    const app = new App();
    const { mockMessageTable, mockSummaryTable } = createMocks(app);

    // Test that the stack can be created with additional stack props
    const stack = new SummarizationStack(app, 'StackPropsTestStack', {
      messageTable: mockMessageTable,
      summaryTable: mockSummaryTable,
      description: 'Test summarization stack',
      env: {
        account: '123456789012',
        region: 'us-west-2'
      }
    });

    expect(stack).toBeDefined();
    expect(stack.stackName).toBe('StackPropsTestStack');
  });
});
