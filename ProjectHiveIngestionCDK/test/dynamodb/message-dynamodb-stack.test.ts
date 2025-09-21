import { App, RemovalPolicy, Stack } from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { MessageDynamoDbStack } from '../../lib/dynamodb/messages-dynamodb-stack';

describe('MessageDynamoDbStack', () => {
  it('creates a DynamoDB table with correct configuration', () => {
    const app = new App();
    const stack = new Stack(app, 'TestStack');

    new MessageDynamoDbStack(stack, 'MessageDdb', {
      removalPolicy: RemovalPolicy.DESTROY,
    });

    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::DynamoDB::Table', {
      TableName: 'messages',
      BillingMode: 'PAY_PER_REQUEST',
      StreamSpecification: {
        StreamViewType: 'NEW_AND_OLD_IMAGES',
      },
      KeySchema: [
        {
          AttributeName: 'id',
          KeyType: 'HASH',
        }
      ],
      AttributeDefinitions: [
        {
          AttributeName: 'id',
          AttributeType: 'S',
        }
      ]
    });
  });
});
