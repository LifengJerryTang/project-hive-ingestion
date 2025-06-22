// lib/dynamodb/messages-dynamodb.ts

import { RemovalPolicy } from 'aws-cdk-lib';
import { AttributeType, BillingMode, ITable, StreamViewType, Table} from 'aws-cdk-lib/aws-dynamodb';
import { Construct } from 'constructs';

export interface MessageDynamoDbProps {
  readonly removalPolicy: RemovalPolicy;
}

export class MessageDynamoDbStack extends Construct {
  public readonly messagesTable: ITable;

  constructor(scope: Construct, id: string, props: MessageDynamoDbProps) {
    super(scope, id);

    this.messagesTable = new Table(this, 'MessageTable', {
      tableName: 'messages', // hardcoded to match DAO and design
      partitionKey: {
        name: 'messageId',
        type: AttributeType.STRING,
      },
      billingMode: BillingMode.PAY_PER_REQUEST,
      stream: StreamViewType.NEW_AND_OLD_IMAGES,
      removalPolicy: props.removalPolicy,
    });
  }
}
