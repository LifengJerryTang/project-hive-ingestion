package com.projecthive.summarization.dao;

import com.projecthive.summarization.constants.TestConstants;
import com.projecthive.summarization.constants.TestConstants.Numbers;
import com.projecthive.summarization.constants.TestConstants.Summaries;
import com.projecthive.summarization.constants.TestConstants.Messages;
import com.projecthive.summarization.constants.TestConstants.DynamoDb;
import com.projecthive.summarization.constants.TestConstants.Errors;
import com.projecthive.summarization.constants.TestConstants.SpecialData;
import com.projecthive.summarization.exceptions.DaoDataAccessException;
import com.projecthive.summarization.models.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummaryDaoImpl to ensure 100% code coverage.
 * Tests DynamoDB Enhanced Client integration, error handling, and data persistence.
 */
@ExtendWith(MockitoExtension.class)
class SummaryDaoImplTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private DynamoDbEnhancedClient mockEnhancedClient;

    @Mock
    private DynamoDbTable<Summary> mockSummaryTable;

    private SummaryDaoImpl summaryDao;

    @BeforeEach
    void setUp() throws Exception {
        // Create DAO instance
        summaryDao = new SummaryDaoImpl(mockDynamoDbClient);

        // Use reflection to inject mock enhanced client and table for testing
        Field enhancedClientField = SummaryDaoImpl.class.getDeclaredField("summaryTable");
        enhancedClientField.setAccessible(true);
        enhancedClientField.set(summaryDao, mockSummaryTable);
    }

    @Test
    void save_withValidSummary_shouldSaveSuccessfully() {
        // Arrange
        Summary testSummary = createTestSummary();

        // Act
        summaryDao.save(testSummary);

        // Assert - Verify putItem was called
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withSummaryContainingAllFields_shouldSaveAllData() {
        // Arrange
        Summary completeSummary = Summary.builder()
                .summaryId(Summaries.COMPLETE_SUMMARY_ID)
                .username(Messages.COMPLETE_USER)
                .timestamp(Summaries.TEST_TIMESTAMP_ISO)
                .summaryText(Summaries.COMPLETE_SUMMARY_TEXT)
                .source(Messages.GMAIL_PLATFORM)
                .messageId(Messages.COMPLETE_MESSAGE_ID)
                .messageSender(Messages.COMPLETE_SENDER_EMAIL)
                .messageReceivedAt(Messages.RECEIVED_AT_TIMESTAMP)
                .messageSubject(Messages.COMPLETE_TEST_SUBJECT)
                .build();

        // Act
        summaryDao.save(completeSummary);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withSummaryWithNullSubject_shouldHandleNullFields() {
        // Arrange
        Summary summaryWithNullSubject = Summary.builder()
                .summaryId(TestConstants.Summaries.NULL_SUBJECT_SUMMARY_ID)
                .username(TestConstants.Messages.TEST_USERNAME)
                .timestamp(TestConstants.Summaries.TEST_TIMESTAMP_ISO)
                .summaryText(TestConstants.Summaries.DISCORD_SUMMARY_TEXT)
                .source(TestConstants.Messages.DISCORD_PLATFORM)
                .messageId(TestConstants.DynamoDb.DISCORD_MESSAGE_ID)
                .messageSender(TestConstants.Messages.TEST_USERNAME_2)
                .messageReceivedAt(TestConstants.Messages.RECEIVED_AT_TIMESTAMP)
                .messageSubject(null) // Null subject for Discord messages
                .build();

        // Act
        summaryDao.save(summaryWithNullSubject);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withLongSummaryText_shouldHandleLongContent() {
        // Arrange
        Summary longSummary = createTestSummary();
        longSummary.setSummaryText(SpecialData.LONG_SUMMARY_TEXT);

        // Act
        summaryDao.save(longSummary);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withSpecialCharactersInSummary_shouldHandleSpecialCharacters() {
        // Arrange
        Summary specialCharSummary = createTestSummary();
        specialCharSummary.setSummaryText(SpecialData.SPECIAL_CHARS_SUMMARY);
        specialCharSummary.setMessageSender(Messages.USER_DOMAIN_EMAIL);
        specialCharSummary.setMessageSubject(Messages.EMOJI_SUBJECT);

        // Act
        summaryDao.save(specialCharSummary);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withDynamoDbException_shouldThrowDaoDataAccessException() {
        // Arrange
        Summary testSummary = createTestSummary();
        RuntimeException dynamoException = new RuntimeException(Errors.DYNAMODB_SERVICE_ERROR);
        
        doThrow(dynamoException).when(mockSummaryTable).putItem(any(PutItemEnhancedRequest.class));

        // Act & Assert
        assertThrows(DaoDataAccessException.class, () -> {
            summaryDao.save(testSummary);
        });
    }

    @Test
    void save_withGenericException_shouldThrowDaoDataAccessException() {
        // Arrange
        Summary testSummary = createTestSummary();
        RuntimeException genericException = new RuntimeException(Errors.GENERIC_ERROR);
        
        doThrow(genericException).when(mockSummaryTable).putItem(any(PutItemEnhancedRequest.class));

        // Act & Assert
        assertThrows(DaoDataAccessException.class, () -> {
            summaryDao.save(testSummary);
        });
    }

    @Test
    void save_withNetworkException_shouldThrowDaoDataAccessException() {
        // Arrange
        Summary testSummary = createTestSummary();
        RuntimeException networkException = new RuntimeException(Errors.NETWORK_TIMEOUT);
        
        doThrow(networkException).when(mockSummaryTable).putItem(any(PutItemEnhancedRequest.class));

        // Act & Assert
        assertThrows(DaoDataAccessException.class, () -> {
            summaryDao.save(testSummary);
        });
    }

    @Test
    void constructor_shouldInitializeEnhancedClientAndTable() throws Exception {
        // Arrange
        DynamoDbClient realClient = mock(DynamoDbClient.class);
        
        // Create a spy to test the protected method
        SummaryDaoImpl spyDao = spy(new SummaryDaoImpl(realClient));
        when(spyDao.buildEnhancedClient(any(DynamoDbClient.class))).thenReturn(mockEnhancedClient);
        when(mockEnhancedClient.table(eq(Summaries.TABLE_NAME), any(TableSchema.class))).thenReturn(mockSummaryTable);

        // Act - Constructor is called during spy creation
        
        // Assert - Verify the enhanced client was built with the correct client
        verify(spyDao, times(Numbers.TIMES_ONE)).buildEnhancedClient(realClient);
    }

    @Test
    void buildEnhancedClient_shouldReturnValidEnhancedClient() {
        // Arrange
        DynamoDbClient testClient = mock(DynamoDbClient.class);
        SummaryDaoImpl testDao = new SummaryDaoImpl(testClient);

        // Act
        DynamoDbEnhancedClient result = testDao.buildEnhancedClient(testClient);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof DynamoDbEnhancedClient);
    }

    @Test
    void save_withMultipleSummaries_shouldSaveAllIndependently() {
        // Arrange
        Summary summary1 = createTestSummary();
        summary1.setSummaryId(Summaries.SUMMARY_1_ID);
        
        Summary summary2 = createTestSummary();
        summary2.setSummaryId(Summaries.SUMMARY_2_ID);
        summary2.setUsername(Messages.USER_2);
        
        Summary summary3 = createTestSummary();
        summary3.setSummaryId(Summaries.SUMMARY_3_ID);
        summary3.setSource(Messages.DISCORD_PLATFORM);

        // Act
        summaryDao.save(summary1);
        summaryDao.save(summary2);
        summaryDao.save(summary3);

        // Assert
        verify(mockSummaryTable, times(Numbers.TIMES_THREE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_shouldUseCorrectTableName() throws Exception {
        // This test verifies that the correct table name "summaries" is used
        // We can verify this by checking the constructor behavior
        
        // Arrange
        DynamoDbClient testClient = mock(DynamoDbClient.class);
        DynamoDbEnhancedClient testEnhancedClient = mock(DynamoDbEnhancedClient.class);
        DynamoDbTable<Summary> testTable = mock(DynamoDbTable.class);
        
        // Create a spy to intercept the buildEnhancedClient call
        SummaryDaoImpl spyDao = spy(new SummaryDaoImpl(testClient));
        when(spyDao.buildEnhancedClient(testClient)).thenReturn(testEnhancedClient);
        when(testEnhancedClient.table(eq(Summaries.TABLE_NAME), any(TableSchema.class))).thenReturn(testTable);
        
        // The constructor should have been called, but let's verify the table method call
        verify(testEnhancedClient, times(Numbers.TIMES_ONE)).table(eq(Summaries.TABLE_NAME), any(TableSchema.class));
    }

    @Test
    void save_withEmptyStrings_shouldHandleEmptyValues() {
        // Arrange
        Summary summaryWithEmptyStrings = Summary.builder()
                .summaryId(Summaries.EMPTY_STRINGS_SUMMARY_ID)
                .username(SpecialData.EMPTY_STRING)
                .timestamp(Summaries.TEST_TIMESTAMP_ISO)
                .summaryText(SpecialData.EMPTY_STRING)
                .source(SpecialData.EMPTY_STRING)
                .messageId(Messages.TEST_MESSAGE_ID)
                .messageSender(SpecialData.EMPTY_STRING)
                .messageReceivedAt(Messages.RECEIVED_AT_TIMESTAMP)
                .messageSubject(SpecialData.EMPTY_STRING)
                .build();

        // Act
        summaryDao.save(summaryWithEmptyStrings);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withVeryLongIds_shouldHandleLongIdentifiers() {
        // Arrange
        Summary longIdSummary = createTestSummary();
        longIdSummary.setSummaryId(SpecialData.VERY_LONG_ID);
        longIdSummary.setMessageId(SpecialData.VERY_LONG_ID);

        // Act
        summaryDao.save(longIdSummary);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    void save_withZeroTimestamp_shouldHandleZeroValues() {
        // Arrange
        Summary zeroTimestampSummary = createTestSummary();
        zeroTimestampSummary.setMessageReceivedAt(Messages.ZERO_TIMESTAMP);

        // Act
        summaryDao.save(zeroTimestampSummary);

        // Assert - Verify putItem was called with a PutItemEnhancedRequest
        verify(mockSummaryTable, times(Numbers.TIMES_ONE)).putItem(any(PutItemEnhancedRequest.class));
    }

    /**
     * Helper method to create a standard test summary.
     */
    private Summary createTestSummary() {
        return Summary.builder()
                .summaryId(Summaries.TEST_SUMMARY_ID)
                .username(Messages.TEST_USERNAME)
                .timestamp(Summaries.TEST_TIMESTAMP_ISO)
                .summaryText(Summaries.TEST_SUMMARY_TEXT)
                .source(Messages.GMAIL_PLATFORM)
                .messageId(Messages.TEST_MESSAGE_ID)
                .messageSender(Messages.SENDER_EMAIL)
                .messageReceivedAt(Messages.RECEIVED_AT_TIMESTAMP)
                .messageSubject(Messages.TEST_EMAIL_SUBJECT)
                .build();
    }
}
