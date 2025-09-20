package com.projecthive.summarization.constants;

/**
 * Test constants used across all test classes to ensure consistency and maintainability.
 * This class contains all hard-coded values extracted from test files to provide a single
 * source of truth for test data.
 */
public final class TestConstants {

    private TestConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Model and AI-related constants for Bedrock testing.
     */
    public static final class Models {
        public static final String CLAUDE_3_SONNET_MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
        public static final String CLAUDE_3_HAIKU_MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0";
        public static final String CLAUDE_INSTANT_MODEL_ID = "anthropic.claude-instant-v1";
        public static final String CUSTOM_MODEL_ID = "custom-model-id";
        
        public static final String ANTHROPIC_VERSION = "bedrock-2023-05-31";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String ACCEPT_JSON = "application/json";
        
        // Model parameters
        public static final int MAX_TOKENS_DEFAULT = 500;
        public static final int MAX_TOKENS_HIGH = 1000;
        public static final int MAX_TOKENS_LOW = 100;
        
        public static final double TEMPERATURE_DEFAULT = 0.3;
        public static final double TEMPERATURE_HIGH = 0.7;
        public static final double TEMPERATURE_LOW = 0.0;
        
        public static final int TOP_K_DEFAULT = 250;
        public static final int TOP_K_HIGH = 100;
        public static final int TOP_K_LOW = 1;
        
        public static final double TOP_P_DEFAULT = 0.9;
        public static final double TOP_P_HIGH = 0.95;
        public static final double TOP_P_LOW = 0.1;
        
        public static final String USER_ROLE = "user";
    }

    /**
     * Test message data constants.
     */
    public static final class Messages {
        public static final String TEST_MESSAGE_ID = "test-message-id";
        public static final String TEST_MESSAGE_ID_2 = "msg-456";
        public static final String DIFFERENT_MESSAGE_ID = "different-id";
        
        public static final String TEST_USERNAME = "testuser";
        public static final String TEST_USERNAME_2 = "testuser2";
        public static final String USER_2 = "user2";
        public static final String COMPLETE_USER = "completeuser";
        public static final String COMPLETE_MESSAGE_ID = "complete-message-id";
        
        public static final String GMAIL_PLATFORM = "gmail";
        public static final String DISCORD_PLATFORM = "discord";
        public static final String SLACK_PLATFORM = "slack";
        
        public static final String MSG_PLATFORM_PREFIX = "msg-";
        public static final String PLATFORM_ID_SUFFIX = "-123";
        
        public static final String GMAIL_PLATFORM_ID = "gmail-123";
        public static final String DISCORD_PLATFORM_ID = "discord-789";
        public static final String SLACK_PLATFORM_ID = "slack-456";
        
        public static final String SENDER_EMAIL = "sender@example.com";
        public static final String RECIPIENT_EMAIL = "recipient@example.com";
        public static final String TEST_EMAIL = "test@example.com";
        public static final String COMPLETE_SENDER_EMAIL = "complete@sender.com";
        public static final String USER_DOMAIN_EMAIL = "user@domain.com";
        
        public static final String DISCORD_CHANNEL = "#general";
        public static final String DISCORD_CHANNEL_NAME = "#channel-name";
        public static final String SLACK_WORKSPACE_CHANNEL = "#workspace-channel";
        
        public static final String TEST_EMAIL_SUBJECT = "Test Email Subject";
        public static final String IMPORTANT_EMAIL_SUBJECT = "Important Email Subject";
        public static final String COMPLETE_TEST_SUBJECT = "Complete Test Subject";
        public static final String THREAD_SUBJECT = "Thread Subject";
        public static final String EMOJI_SUBJECT = "Subject with émojis 🎉 and spëcial chars";
        
        public static final String TEST_MESSAGE_BODY = "This is a test email body with important information that needs to be summarized.";
        public static final String SIMPLE_TEST_BODY = "This is a test message.";
        public static final String DISCORD_QUESTION_BODY = "Hey everyone, quick question about the project!";
        public static final String GMAIL_PLATFORM_BODY = "Test message for gmail";
        public static final String DISCORD_PLATFORM_BODY = "Test message for discord";
        public static final String SLACK_PLATFORM_BODY = "Test message for slack";
        
        public static final long TEST_TIMESTAMP = 1234567890L;
        public static final long CURRENT_TIMESTAMP = System.currentTimeMillis();
        public static final long RECEIVED_AT_TIMESTAMP = 1695218200000L;
        public static final long ZERO_TIMESTAMP = 0L;
    }

    /**
     * Summary-related test constants.
     */
    public static final class Summaries {
        public static final String TEST_SUMMARY_ID = "test-summary-id";
        public static final String COMPLETE_SUMMARY_ID = "complete-summary-id";
        public static final String NULL_SUBJECT_SUMMARY_ID = "null-subject-summary";
        public static final String EMPTY_STRINGS_SUMMARY_ID = "empty-strings-summary";
        public static final String SUMMARY_1_ID = "summary-1";
        public static final String SUMMARY_2_ID = "summary-2";
        public static final String SUMMARY_3_ID = "summary-3";
        
        public static final String TEST_TIMESTAMP_ISO = "2023-09-20T15:30:00Z";
        
        public static final String TEST_SUMMARY_TEXT = "• Test summary point 1\n• Test summary point 2\n• Test summary point 3";
        public static final String COMPLETE_SUMMARY_TEXT = "• Complete summary with all fields\n• Contains comprehensive information\n• Includes all metadata";
        public static final String DISCORD_SUMMARY_TEXT = "• Summary without subject\n• Discord message example";
        public static final String EXPECTED_MODEL_OUTPUT = "• User received an important email\n• Meeting scheduled for tomorrow\n• Action required by end of week";
        public static final String DISCORD_MODEL_OUTPUT = "• User asked a question about the project\n• Message sent to general channel";
        public static final String LONG_TECHNICAL_OUTPUT = "• Long technical message with specifications\n• Contains business requirements and dates\n• References other documents and systems";
        public static final String SPECIAL_CHARS_OUTPUT = "• Message contains special characters\n• Various symbols and punctuation included";
        public static final String HIGH_PRIORITY_OUTPUT = "• High priority urgent message\n• Requires immediate attention";
        public static final String PLATFORM_SPECIFIC_OUTPUT = "Platform-specific summary";
        public static final String COMPLEX_RESPONSE = "Complex response";
        public static final String SHORT_RESPONSE = "Short response";
        public static final String TEST_SUMMARY_RESPONSE = "Test summary";
        
        public static final String TABLE_NAME = "summaries";
    }

    /**
     * DynamoDB event and attribute constants.
     */
    public static final class DynamoDb {
        public static final String INSERT_EVENT = "INSERT";
        public static final String MODIFY_EVENT = "MODIFY";
        public static final String REMOVE_EVENT = "REMOVE";
        
        public static final String ID_ATTRIBUTE = "id";
        public static final String USERNAME_ATTRIBUTE = "username";
        public static final String PLATFORM_ATTRIBUTE = "platform";
        
        public static final String MESSAGE_ID_ATTRIBUTE = "message-id";
        public static final String DISCORD_MESSAGE_ID = "discord-message-id";
        
        public static final String BINARY_CONTENT = "binary content";
        public static final String BINARY_1 = "binary1";
        public static final String BINARY_2 = "binary2";
        public static final String BINARY_3 = "binary3";
        public static final String BINARY_TEST = "binary";
    }

    /**
     * Special characters and edge case test data.
     */
    public static final class SpecialData {
        public static final String SPECIAL_CHARACTERS = "@#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        public static final String SPECIAL_CHARS_MESSAGE = "Message with special chars: @#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        public static final String SPECIAL_CHARS_SUMMARY = "• Summary with special chars: @#$%^&*(){}[]|\\:;\"'<>,.?/~`\n• Unicode characters: 你好 🚀 ñáéíóú\n• Newlines and\ttabs";
        
        public static final String UNICODE_CHARACTERS = "你好世界 🌍 ñáéíóú àèìòù âêîôû";
        public static final String UNICODE_TEST = "Unicode test: 你好世界 🌍 ñáéíóú àèìòù âêîôû";
        public static final String UNICODE_HELLO = "你好";
        public static final String UNICODE_ROCKET = "🚀";
        public static final String UNICODE_ACCENTS = "ñáéíóú";
        
        public static final String COMPLEX_UNICODE_MESSAGE = "Complex message with special characters: @#$%^&*(){}[]|\\:;\"'<>,.?/~`\nMultiple lines\nAnd unicode: 你好";
        
        public static final String JSON_LIKE_STRING = "{\"key\": \"value\", \"number\": 123, \"boolean\": true}";
        public static final String NEWLINE_STRING = "Line 1\nLine 2\r\nLine 3\tTabbed";
        
        public static final String LONG_MESSAGE_BODY = "This is a very long message body that contains multiple sentences and paragraphs. " +
                "It includes detailed information about various topics including technical specifications, " +
                "business requirements, and implementation details. The message also contains specific " +
                "dates, numbers, and references to other documents and systems that need to be summarized " +
                "effectively by the AI model to provide a concise overview of the key points.";
        
        public static final String LONG_SUMMARY_TEXT = "• This is a very long summary text that contains multiple bullet points and detailed information about the original message content. " +
                "It includes technical specifications, business requirements, implementation details, and various other important aspects that need to be preserved. " +
                "The summary maintains all critical information while being concise and readable for end users.\n" +
                "• Second bullet point with additional context and information that provides more details about the message content and its implications.\n" +
                "• Third bullet point that concludes the summary with final thoughts and action items that may be required.";
        
        public static final String VERY_LONG_ID = "a".repeat(1000);
        public static final String EMPTY_STRING = "";
    }

    /**
     * Error messages and exception-related constants.
     */
    public static final class Errors {
        public static final String DYNAMODB_SERVICE_ERROR = "DynamoDB service error";
        public static final String JSON_SERIALIZATION_ERROR = "JSON serialization error";
        public static final String BEDROCK_SERVICE_ERROR = "Bedrock service error";
        public static final String GENERIC_ERROR = "Generic error";
        public static final String NETWORK_TIMEOUT = "Network timeout";
        public static final String FAILED_TO_SAVE_SUMMARY = "Failed to save summary to DynamoDB";
        public static final String FAILED_TO_INVOKE_MODEL = "Failed to invoke Bedrock model";
        public static final String FAILED_TO_INJECT_CONTROLLER = "Failed to inject mock controller";
        public static final String FAILED_TO_ACCESS_CONTROLLER = "Failed to access controller field";
    }

    /**
     * Test response data and JSON structures.
     */
    public static final class Responses {
        public static final String JSON_RESPONSE_WITH_BULLETS = "{\"content\":[{\"text\":\"• Test summary point 1\\n• Test summary point 2\"}]}";
        public static final String COMPLEX_JSON_RESPONSE = "{\"content\":[{\"text\":\"Complex response\"}]}";
        public static final String SHORT_JSON_RESPONSE = "{\"content\":[{\"text\":\"Short response\"}]}";
        public static final String GENERIC_JSON_RESPONSE = "{\"content\":[{\"text\":\"Response\"}]}";
        public static final String EMPTY_RESPONSE = "";
    }

    /**
     * Numeric constants used in tests.
     */
    public static final class Numbers {
        public static final int TIMES_ONE = 1;
        public static final int TIMES_TWO = 2;
        public static final int TIMES_THREE = 3;
        public static final int TIMES_FOUR = 4;
        
        public static final double DELTA_PRECISION = 0.001;
        
        // Large numbers for testing
        public static final String LARGE_INT_STRING = "9223372036854775807"; // Long.MAX_VALUE
        public static final String LARGE_FLOAT_STRING = "1.7976931348623157E308"; // Close to Double.MAX_VALUE
        public static final String PRECISION_FLOAT_STRING = "3.141592653589793238462643383279502884197";
        
        // Test numeric strings
        public static final String NUMBER_42 = "42";
        public static final String NUMBER_123 = "123";
        public static final String NUMBER_456 = "456";
        public static final String NUMBER_789 = "789";
        public static final String NUMBER_1000 = "1000";
        public static final String NEGATIVE_123 = "-123";
        public static final String FLOAT_PI = "3.14159";
        public static final String LONG_TIMESTAMP = "1695218200000";
        
        // Array test data
        public static final String[] STRING_SET_VALUES = {"value1", "value2", "value3"};
        public static final String[] STRING_SET_SIMPLE = {"set1", "set2"};
        public static final String[] NUMBER_SET_VALUES = {"1", "2", "3", "42"};
        public static final String[] NUMBER_SET_SIMPLE = {"10", "20"};
    }

    /**
     * Prompt and content constants.
     */
    public static final class Prompts {
        public static final String SUMMARIZE_PROMPT_PREFIX = "Summarize the following message in 2–3 bullet points:";
        public static final String SHORT_MESSAGE_CONTENT = "Short message";
        public static final String TEST_MESSAGE_CONTENT = "Summarize the following message in 2–3 bullet points:\n\nThis is a test message.";
    }

    /**
     * Metadata and additional test data.
     */
    public static final class Metadata {
        public static final String PRIORITY_KEY = "priority";
        public static final String PRIORITY_HIGH = "high";
        public static final String CATEGORY_KEY = "category";
        public static final String CATEGORY_URGENT = "urgent";
    }

    /**
     * Field names used in reflection-based testing.
     */
    public static final class FieldNames {
        public static final String CONTROLLER_FIELD = "controller";
        public static final String CLIENT_FIELD = "client";
        public static final String OBJECT_MAPPER_FIELD = "objectMapper";
        public static final String SUMMARY_TABLE_FIELD = "summaryTable";
    }

    /**
     * StreamRecordConverter test constants.
     */
    public static final class StreamConverter {
        // Test message data for stream conversion
        public static final String COMPLETE_TEST_MESSAGE_ID = "test-message-id";
        public static final String COMPLETE_TEST_USERNAME = "testuser";
        public static final String COMPLETE_TEST_PLATFORM = "gmail";
        public static final String COMPLETE_PLATFORM_MESSAGE_ID = "gmail-123";
        public static final String COMPLETE_RECIPIENT = "recipient@example.com";
        public static final String COMPLETE_SENDER = "sender@example.com";
        public static final String COMPLETE_SUBJECT = "Test Subject";
        public static final String COMPLETE_BODY = "This is a test message body";
        public static final long COMPLETE_RECEIVED_AT = 1695218200000L;
        
        // Minimal record data
        public static final String MINIMAL_ID = "minimal-id";
        public static final String MINIMAL_USERNAME = "minimaluser";
        public static final String MINIMAL_PLATFORM = "discord";
        public static final String MINIMAL_PLATFORM_MESSAGE_ID = "discord-456";
        public static final String MINIMAL_RECIPIENT = "#general";
        public static final String MINIMAL_SENDER = "discorduser";
        public static final String MINIMAL_BODY = "Quick message";
        public static final long MINIMAL_RECEIVED_AT = 1695218300000L;
        
        // Test values for different attribute types
        public static final String STRING_VALUE = "string-value";
        public static final String SPECIAL_CHARS_PLATFORM = "Special chars: @#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        public static final String UNICODE_PLATFORM_ID = "Unicode: 你好 🚀 ñáéíóú";
        
        // Numeric test values
        public static final String LONG_TIMESTAMP_STRING = "1695218200000";
        public static final String INT_VALUE_STRING = "42";
        public static final String FLOAT_VALUE_STRING = "3.14159";
        public static final String NEGATIVE_VALUE_STRING = "-123";
        
        // Collection test values
        public static final String[] TAG_VALUES = {"tag1", "tag2", "urgent", "important"};
        public static final String[] SCORE_VALUES = {"1", "2", "3", "42"};
        
        // Binary test data
        public static final String BINARY_CONTENT = "binary data content";
        public static final String BINARY_1_CONTENT = "binary1";
        public static final String BINARY_2_CONTENT = "binary2";
        
        // Nested structure test data
        public static final String NESTED_STRING_VALUE = "nested-string";
        public static final String NESTED_NUMBER_VALUE = "123";
        public static final String LIST_STRING_VALUE = "list-string";
        public static final String LIST_NUMBER_VALUE = "456";
        public static final String DEEPLY_NESTED_STRING = "deeply-nested-string";
        public static final String LIST_ITEM_VALUE = "list-item";
        
        // Attribute field names
        public static final String ID_FIELD = "id";
        public static final String USERNAME_FIELD = "username";
        public static final String PLATFORM_FIELD = "platform";
        public static final String PLATFORM_MESSAGE_ID_FIELD = "platformMessageId";
        public static final String RECIPIENT_FIELD = "recipient";
        public static final String SENDER_FIELD = "sender";
        public static final String SUBJECT_FIELD = "subject";
        public static final String BODY_FIELD = "body";
        public static final String RECEIVED_AT_FIELD = "receivedAt";
        public static final String METADATA_FIELD = "metadata";
        
        // Error messages
        public static final String UNSUPPORTED_ATTRIBUTE_ERROR = "Unsupported AttributeValue type in stream record";
    }

    /**
     * DynamoDbUtils test constants.
     */
    public static final class DynamoDbTestData {
        // String test values
        public static final String TEST_STRING = "test-string";
        public static final String MIXED_STRING = "mixed-string";
        public static final String SPECIAL_CHARS_STRING = "Special chars: @#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        
        // Field names for testing
        public static final String STRING_FIELD = "stringField";
        public static final String EMPTY_STRING_FIELD = "emptyStringField";
        public static final String SPECIAL_CHARS_FIELD = "specialCharsField";
        public static final String UNICODE_FIELD = "unicodeField";
        public static final String INT_FIELD = "intField";
        public static final String LONG_FIELD = "longField";
        public static final String FLOAT_FIELD = "floatField";
        public static final String NEGATIVE_FIELD = "negativeField";
        public static final String MESSAGE_COUNT_FIELD = "messageCount";
        public static final String PRIORITY_FIELD = "priority";
        public static final String TRUE_FIELD = "trueField";
        public static final String FALSE_FIELD = "falseField";
        public static final String NULL_FIELD = "nullField";
        public static final String ANOTHER_NULL_FIELD = "anotherNullField";
        public static final String STRING_SET_FIELD = "stringSetField";
        public static final String EMPTY_STRING_SET_FIELD = "emptyStringSetField";
        public static final String NUMBER_SET_FIELD = "numberSetField";
        public static final String EMPTY_NUMBER_SET_FIELD = "emptyNumberSetField";
        public static final String BINARY_FIELD = "binaryField";
        public static final String BINARY_SET_FIELD = "binarySetField";
        public static final String MAP_FIELD = "mapField";
        public static final String LIST_FIELD = "listField";
        public static final String COMPLEX_FIELD = "complexField";
        public static final String EMPTY_MAP_FIELD = "emptyMapField";
        public static final String EMPTY_LIST_FIELD = "emptyListField";
        public static final String NUMBER_FIELD = "numberField";
        public static final String BOOL_FIELD = "boolField";
        public static final String JSON_FIELD = "jsonField";
        public static final String NEWLINE_FIELD = "newlineField";
        public static final String LARGE_INT_FIELD = "largeIntField";
        public static final String LARGE_FLOAT_FIELD = "largeFloatField";
        public static final String PRECISION_FLOAT_FIELD = "precisionFloatField";
        
        // Nested field names
        public static final String NESTED_STRING_FIELD = "nestedString";
        public static final String NESTED_NUMBER_FIELD = "nestedNumber";
        public static final String NESTED_BOOL_FIELD = "nestedBool";
        public static final String NESTED_LIST_FIELD = "nestedList";
        public static final String OUTER_STRING_FIELD = "outerString";
        public static final String INNER_STRING_FIELD = "innerString";
        
        // Test values
        public static final String UNICODE_TEST_VALUE = "Unicode: 你好 🚀 ñáéíóú";
        public static final String NESTED_STRING_VALUE = "nested-string";
        public static final String OUTER_STRING_VALUE = "outer-string";
        public static final String INNER_STRING_VALUE = "deeply-nested-string";
        public static final String LIST_STRING_VALUE = "list-string";
        public static final String LIST_ITEM_VALUE = "list-item";
        
        // Collection field names for testing
        public static final String TAGS_FIELD = "tags";
        public static final String SCORES_FIELD = "scores";
        public static final String ATTACHMENTS_FIELD = "attachments";
        public static final String ATTACHMENT_FIELD = "attachment";
        public static final String UNSUPPORTED_FIELD = "unsupported";
        public static final String COMPLEX_DATA_FIELD = "complexData";
        public static final String EMPTY_TAGS_FIELD = "emptyTags";
        public static final String EMPTY_SCORES_FIELD = "emptyScores";
        public static final String EMPTY_ATTACHMENTS_FIELD = "emptyAttachments";
        public static final String EMPTY_METADATA_FIELD = "emptyMetadata";
        public static final String IS_READ_FIELD = "isRead";
        public static final String IS_ARCHIVED_FIELD = "isArchived";
    }

    /**
     * Additional test data constants for various test scenarios.
     */
    public static final class TestData {
        // SummarizationHandlerTest constants
        public static final String TEST_SUBJECT_SIMPLE = "Test Subject";
        public static final String TEST_MESSAGE_BODY_SIMPLE = "Test message body";
        
        // SummarizationControllerTest constants
        public static final String MSG_456 = "msg-456";
        public static final String TESTUSER2 = "testuser2";
        public static final String DISCORD_789 = "discord-789";
        public static final String GENERAL_CHANNEL = "#general";
        public static final String USER2 = "user2";
        public static final String DISCORD_QUESTION = "Hey everyone, quick question about the project!";
        public static final String DISCORD_RESPONSE = "• User asked a question about the project\n• Message sent to general channel";
        public static final String EMAIL_EXAMPLE = "email@example.com";
        public static final String CHANNEL_NAME = "#channel-name";
        public static final String WORKSPACE_CHANNEL = "#workspace-channel";
        public static final String THREAD_SUBJECT_SIMPLE = "Thread Subject";
        public static final String MSG_PREFIX = "msg-";
        public static final String TEST_MESSAGE_FOR_PREFIX = "Test message for ";
        public static final String PLATFORM_SPECIFIC_SUMMARY = "Platform-specific summary";
        public static final String DIFFERENT_ID = "different-id";
        public static final String TEST_SUMMARY_SIMPLE = "Test summary";
        
        // Test platform message IDs
        public static final String MSG_GMAIL = "msg-gmail";
        public static final String MSG_DISCORD = "msg-discord";
        public static final String MSG_SLACK = "msg-slack";
        
        // Required field defaults for testing
        public static final String TEST_ID_DEFAULT = "test-id";
        public static final String TEST_PLATFORM_DEFAULT = "test-platform";
        public static final String TEST_PLATFORM_ID_DEFAULT = "test-platform-id";
        public static final String TEST_RECIPIENT_DEFAULT = "test-recipient";
        public static final String TEST_SENDER_DEFAULT = "test-sender";
        public static final String TEST_BODY_DEFAULT = "test-body";
    }
}
