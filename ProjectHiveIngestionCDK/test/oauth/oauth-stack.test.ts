import { Template, Match } from 'aws-cdk-lib/assertions';
import { OAuthStack } from '../../lib/oauth/oauth-stack';
import { App } from 'aws-cdk-lib';

describe('OAuthStack', () => {
  test('creates a Gmail OAuth secret with correct properties', () => {
    const stack = new OAuthStack(new App(), 'OAuthStackTest', {});

    const template = Template.fromStack(stack);

    template.hasResourceProperties('AWS::SecretsManager::Secret', {
      Name: 'gmail-oauth-token',
      Description: 'Stores Gmail access and refresh tokens for Project Hive'
    });

    template.hasResource('AWS::SecretsManager::Secret', Match.objectLike({
      DeletionPolicy: 'Retain'
    }));
  });

  test('exposes gmailOAuthSecret as a stack property', () => {
    const stack = new OAuthStack(new App(), 'OAuthStackExportTest', {});
    expect(stack.gmailOAuthSecret).toBeDefined();
    expect(stack.gmailOAuthSecret.secretName).toBeDefined(); // light sanity check
  });
});
