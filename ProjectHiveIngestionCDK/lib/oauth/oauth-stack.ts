import { Stack, StackProps, RemovalPolicy } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';

export class OAuthStack extends Stack {
  public readonly gmailOAuthSecret: secretsmanager.ISecret;

  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, props);

    // Create a new secret in Secrets Manager
    const gmailSecret = new secretsmanager.Secret(this, 'GmailOAuthToken', {
      secretName: 'gmail-oauth-token',
      description: 'Stores Gmail access and refresh tokens for Project Hive',
      removalPolicy: RemovalPolicy.RETAIN, // Prevent deletion in prod accidentally
    });

    // Expose it to other stacks
    this.gmailOAuthSecret = gmailSecret;
  }
}