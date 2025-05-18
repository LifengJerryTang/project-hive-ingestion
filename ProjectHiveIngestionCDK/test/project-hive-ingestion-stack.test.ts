import { ProjectHiveIngestionStack } from '../lib/project-hive-ingestion-stack';
import { App } from 'aws-cdk-lib';

describe('ProjectHiveIngestionStack', () => {
  test('composes all required sub-stacks', () => {
    const app = new App();
    const rootStack = new ProjectHiveIngestionStack(app, 'ProjectHiveRootStack');

    // Check that all child constructs (not nested stacks) exist
    expect(rootStack.node.tryFindChild('EventBridgeSchedulerStack')).toBeDefined();
    expect(rootStack.node.tryFindChild('OAuthStack')).toBeDefined();
    expect(rootStack.node.tryFindChild('GmailIngestionStack')).toBeDefined();
  });
});