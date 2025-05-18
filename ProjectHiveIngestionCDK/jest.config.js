module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',

  // Only run .test.ts files inside the test folder
  testMatch: ['**/test/**/*.test.ts'],

  // Automatically resolve these file types when importing
  moduleFileExtensions: ['ts', 'js', 'json', 'node'],

  // Ignore transpiled CDK output and dependency folders
  testPathIgnorePatterns: ['/node_modules/', '/cdk.out/'],

  // Collect code coverage from all lib files (excluding test and infra-generated files)
  collectCoverage: true,
  collectCoverageFrom: ['lib/**/*.ts', '!lib/**/index.ts'],

  // Output coverage to coverage/ folder
  coverageDirectory: 'coverage',

  // Configure the type of report you want (text + HTML + lcov for CI/CD tools)
  coverageReporters: ['text', 'lcov', 'html'],

  // Show which lines/branches/functions are missing coverage
  coverageThreshold: {
    global: {
      branches: 100,
      functions: 100,
      lines: 100,
      statements: 100,
    }
  },

  // Clean up stack traces for readability
  verbose: true,
  clearMocks: true,
  resetMocks: true
};
