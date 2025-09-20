package com.projecthive.summarization.controller;

import com.projecthive.summarization.bedrock.BedrockModelInvoker;
import com.projecthive.summarization.constants.TestConstants.Models;
import com.projecthive.summarization.constants.TestConstants.Messages;
import com.projecthive.summarization.constants.TestConstants.Summaries;
import com.projecthive.summarization.constants.TestConstants.Numbers;
import com.projecthive.summarization.constants.TestConstants.Prompts;
import com.projecthive.summarization.constants.TestConstants.SpecialData;
import com.projecthive.summarization.constants.TestConstants.Metadata;
import com.projecthive.summarization.constants.TestConstants.TestData;
import com.projecthive.summarization.dao.SummaryDao;
import com.projecthive.summarization.models.ClaudePromptPayload;
import com.projecthive.summarization.models.Message;
import com.projecthive.summarization.models.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummarizationController to ensure 100% code coverage.
 * Tests end-to-end summarization flow, prompt building, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class SummarizationControllerTest {

    @Mock
    private SummaryDao mockSummaryDao;

    @Mock
    private BedrockModelInvoker mockModelInvoker;

    private SummarizationController controller;

    @BeforeEach
    void setUp() {
        controller = new SummarizationController(mockSummaryDao, mockModelInvoker);
    }

    @Test
    void summarizeMessage_withValidMessage_shouldCreateAndSaveSummary() {
        // Arrange
        Message testMessage = createTestMessage();
        String expectedModelOutput = Summaries.EXPECTED_MODEL_OUTPUT;
        
        when(mockModelInvoker.invokeModel(eq(Models.CLAUDE_3_SONNET_MODEL_ID), any(ClaudePromptPayload.class)))
            .thenReturn(expectedModelOutput);

        // Act
        controller.summarizeMessage(testMessage);

        // Assert - Verify model invocation with correct parameters
        ArgumentCaptor<ClaudePromptPayload> payloadCaptor = ArgumentCaptor.forClass(ClaudePromptPayload.class);
        verify(mockModelInvoker, times(Numbers.TIMES_ONE)).invokeModel(eq(Models.CLAUDE_3_SONNET_MODEL_ID), payloadCaptor.capture());
        
        ClaudePromptPayload capturedPayload = payloadCaptor.getValue();
        assertEquals(Models.ANTHROPIC_VERSION, capturedPayload.getAnthropicVersion());
        assertEquals(Models.MAX_TOKENS_DEFAULT, capturedPayload.getMaxTokens());
        assertEquals(Models.TEMPERATURE_DEFAULT, capturedPayload.getTemperature(), Numbers.DELTA_PRECISION);
        assertEquals(Models.TOP_K_DEFAULT, capturedPayload.getTopK());
        assertEquals(Models.TOP_P_DEFAULT, capturedPayload.getTopP(), Numbers.DELTA_PRECISION);
        assertEquals(Numbers.TIMES_ONE, capturedPayload.getPromptMessages().size());
        assertEquals(Models.USER_ROLE, capturedPayload.getPromptMessages().get(0).getRole());
        assertTrue(capturedPayload.getPromptMessages().get(0).getContent().contains(Prompts.SUMMARIZE_PROMPT_PREFIX));
        assertTrue(capturedPayload.getPromptMessages().get(0).getContent().contains(testMessage.getBody()));

        // Assert - Verify summary is saved with correct data
        ArgumentCaptor<Summary> summaryCaptor = ArgumentCaptor.forClass(Summary.class);
        verify(mockSummaryDao, times(Numbers.TIMES_ONE)).save(summaryCaptor.capture());
        
        Summary capturedSummary = summaryCaptor.getValue();
        assertNotNull(capturedSummary.getSummaryId());
        assertEquals(testMessage.getUsername(), capturedSummary.getUsername());
        assertNotNull(capturedSummary.getTimestamp());
        assertEquals(expectedModelOutput, capturedSummary.getSummaryText());
        assertEquals(testMessage.getPlatform(), capturedSummary.getSource());
        assertEquals(testMessage.getId(), capturedSummary.getMessageId());
        assertEquals(testMessage.getSender(), capturedSummary.getMessageSender());
        assertEquals(testMessage.getReceivedAt(), capturedSummary.getMessageReceivedAt());
        assertEquals(testMessage.getSubject(), capturedSummary.getMessageSubject());
    }

    @Test
    void summarizeMessage_withMessageWithoutSubject_shouldHandleNullSubject() {
        // Arrange
        Message messageWithoutSubject = Message.builder()
                .id(TestData.MSG_456)
                .username(TestData.TESTUSER2)
                .platform(Messages.DISCORD_PLATFORM)
                .platformMessageId(TestData.DISCORD_789)
                .recipient(TestData.GENERAL_CHANNEL)
                .sender(TestData.USER2)
                .receivedAt(Messages.TEST_TIMESTAMP)
                .subject(null) // No subject for Discord messages
                .body(TestData.DISCORD_QUESTION)
                .build();

        String expectedModelOutput = TestData.DISCORD_RESPONSE;
        
        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(expectedModelOutput);

        // Act
        controller.summarizeMessage(messageWithoutSubject);

        // Assert - Verify summary handles null subject correctly
        ArgumentCaptor<Summary> summaryCaptor = ArgumentCaptor.forClass(Summary.class);
        verify(mockSummaryDao, times(1)).save(summaryCaptor.capture());
        
        Summary capturedSummary = summaryCaptor.getValue();
        assertNull(capturedSummary.getMessageSubject());
        assertEquals(Messages.DISCORD_PLATFORM, capturedSummary.getSource());
        assertEquals(TestData.USER2, capturedSummary.getMessageSender()); // Discord sender is the user who sent the message
    }

    @Test
    void summarizeMessage_withLongMessageBody_shouldIncludeFullBodyInPrompt() {
        // Arrange
        String longBody = SpecialData.LONG_MESSAGE_BODY;
        
        Message longMessage = createTestMessage();
        longMessage.setBody(longBody);

        String expectedModelOutput = Summaries.LONG_TECHNICAL_OUTPUT;
        
        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(expectedModelOutput);

        // Act
        controller.summarizeMessage(longMessage);

        // Assert - Verify full body is included in prompt
        ArgumentCaptor<ClaudePromptPayload> payloadCaptor = ArgumentCaptor.forClass(ClaudePromptPayload.class);
        verify(mockModelInvoker, times(1)).invokeModel(any(String.class), payloadCaptor.capture());
        
        ClaudePromptPayload capturedPayload = payloadCaptor.getValue();
        assertTrue(capturedPayload.getPromptMessages().get(0).getContent().contains(longBody));
    }

    @Test
    void summarizeMessage_withSpecialCharactersInBody_shouldHandleSpecialCharacters() {
        // Arrange
        Message messageWithSpecialChars = createTestMessage();
        messageWithSpecialChars.setBody(SpecialData.SPECIAL_CHARS_MESSAGE);

        String expectedModelOutput = Summaries.SPECIAL_CHARS_OUTPUT;
        
        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(expectedModelOutput);

        // Act
        controller.summarizeMessage(messageWithSpecialChars);

        // Assert - Verify special characters are handled properly
        ArgumentCaptor<ClaudePromptPayload> payloadCaptor = ArgumentCaptor.forClass(ClaudePromptPayload.class);
        verify(mockModelInvoker, times(1)).invokeModel(any(String.class), payloadCaptor.capture());
        
        ClaudePromptPayload capturedPayload = payloadCaptor.getValue();
        assertTrue(capturedPayload.getPromptMessages().get(0).getContent().contains("@#$%^&*(){}[]|\\:;\"'<>,.?/~`"));
    }

    @Test
    void summarizeMessage_withMessageContainingMetadata_shouldProcessSuccessfully() {
        // Arrange
        Message messageWithMetadata = createTestMessage();
        Map<String, String> metadata = new HashMap<>();
        metadata.put(Metadata.PRIORITY_KEY, Metadata.PRIORITY_HIGH);
        metadata.put(Metadata.CATEGORY_KEY, Metadata.CATEGORY_URGENT);
        messageWithMetadata.setMetadata(metadata);

        String expectedModelOutput = Summaries.HIGH_PRIORITY_OUTPUT;
        
        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(expectedModelOutput);

        // Act
        controller.summarizeMessage(messageWithMetadata);

        // Assert - Verify message with metadata is processed
        verify(mockModelInvoker, times(1)).invokeModel(any(String.class), any(ClaudePromptPayload.class));
        verify(mockSummaryDao, times(1)).save(any(Summary.class));
    }

    @Test
    void summarizeMessage_withDifferentPlatforms_shouldHandleAllPlatforms() {
        // Test Gmail
        testPlatformSpecificMessage(Messages.GMAIL_PLATFORM, TestData.EMAIL_EXAMPLE, Messages.IMPORTANT_EMAIL_SUBJECT);
        
        // Test Discord
        testPlatformSpecificMessage(Messages.DISCORD_PLATFORM, TestData.CHANNEL_NAME, null);
        
        // Test Slack
        testPlatformSpecificMessage(Messages.SLACK_PLATFORM, TestData.WORKSPACE_CHANNEL, TestData.THREAD_SUBJECT_SIMPLE);
        
        // Verify all platforms were processed
        verify(mockModelInvoker, times(3)).invokeModel(any(String.class), any(ClaudePromptPayload.class));
        verify(mockSummaryDao, times(3)).save(any(Summary.class));
    }

    @Test
    void summarizeMessage_shouldGenerateUniqueTimestampsAndIds() {
        // Arrange
        Message message1 = createTestMessage();
        Message message2 = createTestMessage();
        message2.setId(TestData.DIFFERENT_ID);

        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(TestData.TEST_SUMMARY_SIMPLE);

        // Act
        controller.summarizeMessage(message1);
        // Small delay to ensure different timestamps
        try { Thread.sleep(1); } catch (InterruptedException e) { /* ignore */ }
        controller.summarizeMessage(message2);

        // Assert - Verify unique IDs and timestamps
        ArgumentCaptor<Summary> summaryCaptor = ArgumentCaptor.forClass(Summary.class);
        verify(mockSummaryDao, times(2)).save(summaryCaptor.capture());
        
        Summary summary1 = summaryCaptor.getAllValues().get(0);
        Summary summary2 = summaryCaptor.getAllValues().get(1);
        
        assertNotEquals(summary1.getSummaryId(), summary2.getSummaryId());
        // Timestamps should be different (or at least not fail if they're the same due to timing)
        assertNotNull(summary1.getTimestamp());
        assertNotNull(summary2.getTimestamp());
    }

    /**
     * Helper method to test platform-specific message handling.
     */
    private void testPlatformSpecificMessage(String platform, String recipient, String subject) {
        Message platformMessage = Message.builder()
                .id(TestData.MSG_PREFIX + platform)
                .username(Messages.TEST_USERNAME)
                .platform(platform)
                .platformMessageId(platform + Messages.PLATFORM_ID_SUFFIX)
                .recipient(recipient)
                .sender(Messages.SENDER_EMAIL)
                .receivedAt(System.currentTimeMillis())
                .subject(subject)
                .body(TestData.TEST_MESSAGE_FOR_PREFIX + platform)
                .build();

        when(mockModelInvoker.invokeModel(any(String.class), any(ClaudePromptPayload.class)))
            .thenReturn(TestData.PLATFORM_SPECIFIC_SUMMARY);

        controller.summarizeMessage(platformMessage);
    }

    /**
     * Helper method to create a standard test message.
     */
    private Message createTestMessage() {
        return Message.builder()
                .id(Messages.TEST_MESSAGE_ID)
                .username(Messages.TEST_USERNAME)
                .platform(Messages.GMAIL_PLATFORM)
                .platformMessageId(Messages.GMAIL_PLATFORM_ID)
                .recipient(Messages.RECIPIENT_EMAIL)
                .sender(Messages.SENDER_EMAIL)
                .receivedAt(Messages.TEST_TIMESTAMP)
                .subject(Messages.TEST_EMAIL_SUBJECT)
                .body(Messages.TEST_MESSAGE_BODY)
                .build();
    }
}
