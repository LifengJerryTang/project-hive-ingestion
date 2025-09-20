package com.projecthive.summarization;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.projecthive.summarization.constants.TestConstants;
import com.projecthive.summarization.constants.TestConstants.TestData;
import com.projecthive.summarization.controller.SummarizationController;
import com.projecthive.summarization.models.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.projecthive.summarization.utilities.StreamRecordConverter.convertToMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummarizationHandler to ensure 100% code coverage.
 * Tests Lambda event processing, dependency injection, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class SummarizationHandlerTest {

    @Mock
    private Context mockContext;

    @Mock
    private SummarizationController mockController;

    private SummarizationHandler handler;

    @BeforeEach
    void setUp() {
        // Create handler instance - dependency injection will be tested separately
        handler = new SummarizationHandler();
        
        // Use reflection to inject mock controller for testing
        try {
            java.lang.reflect.Field controllerField = SummarizationHandler.class.getDeclaredField(TestConstants.FieldNames.CONTROLLER_FIELD);
            controllerField.setAccessible(true);
            controllerField.set(handler, mockController);
        } catch (Exception e) {
            throw new RuntimeException(TestConstants.Errors.FAILED_TO_INJECT_CONTROLLER, e);
        }
    }

    @Test
    void handleRequest_withInsertEvent_shouldProcessMessage() {
        // Arrange
        DynamodbEvent event = createDynamoDbEvent(TestConstants.DynamoDb.INSERT_EVENT);
        Message expectedMessage = createTestMessage();

        try (MockedStatic<com.projecthive.summarization.utilities.StreamRecordConverter> mockedConverter = 
             mockStatic(com.projecthive.summarization.utilities.StreamRecordConverter.class)) {
            
            mockedConverter.when(() -> convertToMessage(any(DynamodbEvent.DynamodbStreamRecord.class)))
                          .thenReturn(expectedMessage);

            // Act
            Void result = handler.handleRequest(event, mockContext);

            // Assert
            verify(mockController, times(TestConstants.Numbers.TIMES_ONE)).summarizeMessage(expectedMessage);
            assert result == null; // Lambda handler returns null
        }
    }

    @Test
    void handleRequest_withModifyEvent_shouldNotProcessMessage() {
        // Arrange
        DynamodbEvent event = createDynamoDbEvent(TestConstants.DynamoDb.MODIFY_EVENT);

        // Act
        Void result = handler.handleRequest(event, mockContext);

        // Assert
        verify(mockController, never()).summarizeMessage(any(Message.class));
        assert result == null;
    }

    @Test
    void handleRequest_withRemoveEvent_shouldNotProcessMessage() {
        // Arrange
        DynamodbEvent event = createDynamoDbEvent(TestConstants.DynamoDb.REMOVE_EVENT);

        // Act
        Void result = handler.handleRequest(event, mockContext);

        // Assert
        verify(mockController, never()).summarizeMessage(any(Message.class));
        assert result == null;
    }

    @Test
    void handleRequest_withMultipleInsertEvents_shouldProcessAllMessages() {
        // Arrange
        DynamodbEvent event = createMultipleInsertEvents();
        Message expectedMessage = createTestMessage();

        try (MockedStatic<com.projecthive.summarization.utilities.StreamRecordConverter> mockedConverter = 
             mockStatic(com.projecthive.summarization.utilities.StreamRecordConverter.class)) {
            
            mockedConverter.when(() -> convertToMessage(any(DynamodbEvent.DynamodbStreamRecord.class)))
                          .thenReturn(expectedMessage);

            // Act
            Void result = handler.handleRequest(event, mockContext);

            // Assert
            verify(mockController, times(TestConstants.Numbers.TIMES_TWO)).summarizeMessage(expectedMessage);
            assert result == null;
        }
    }

    @Test
    void handleRequest_withMixedEvents_shouldOnlyProcessInserts() {
        // Arrange
        DynamodbEvent event = createMixedEvents();
        Message expectedMessage = createTestMessage();

        try (MockedStatic<com.projecthive.summarization.utilities.StreamRecordConverter> mockedConverter = 
             mockStatic(com.projecthive.summarization.utilities.StreamRecordConverter.class)) {
            
            mockedConverter.when(() -> convertToMessage(any(DynamodbEvent.DynamodbStreamRecord.class)))
                          .thenReturn(expectedMessage);

            // Act
            Void result = handler.handleRequest(event, mockContext);

            // Assert
            // Only 1 INSERT event out of 3 total events should be processed
            verify(mockController, times(TestConstants.Numbers.TIMES_ONE)).summarizeMessage(expectedMessage);
            assert result == null;
        }
    }

    @Test
    void handleRequest_withEmptyRecords_shouldNotProcessAnyMessage() {
        // Arrange
        DynamodbEvent event = new DynamodbEvent();
        event.setRecords(Collections.emptyList());

        // Act
        Void result = handler.handleRequest(event, mockContext);

        // Assert
        verify(mockController, never()).summarizeMessage(any(Message.class));
        assert result == null;
    }

    @Test
    void constructor_shouldInitializeGuiceDependencies() {
        // Act - Create new handler to test constructor
        SummarizationHandler newHandler = new SummarizationHandler();

        // Assert - Verify that controller field is not null (dependency injection worked)
        try {
            java.lang.reflect.Field controllerField = SummarizationHandler.class.getDeclaredField(TestConstants.FieldNames.CONTROLLER_FIELD);
            controllerField.setAccessible(true);
            Object controller = controllerField.get(newHandler);
            assert controller != null : "Controller should be injected by Guice";
        } catch (Exception e) {
            throw new RuntimeException(TestConstants.Errors.FAILED_TO_ACCESS_CONTROLLER, e);
        }
    }

    /**
     * Helper method to create a DynamoDB event with specified event name.
     */
    private DynamodbEvent createDynamoDbEvent(String eventName) {
        DynamodbEvent event = new DynamodbEvent();
        DynamodbEvent.DynamodbStreamRecord record = new DynamodbEvent.DynamodbStreamRecord();
        record.setEventName(eventName);
        
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setNewImage(createTestAttributeMap());
        record.setDynamodb(streamRecord);
        
        event.setRecords(Collections.singletonList(record));
        return event;
    }

    /**
     * Helper method to create multiple INSERT events.
     */
    private DynamodbEvent createMultipleInsertEvents() {
        DynamodbEvent event = new DynamodbEvent();
        
        DynamodbEvent.DynamodbStreamRecord record1 = new DynamodbEvent.DynamodbStreamRecord();
        record1.setEventName(TestConstants.DynamoDb.INSERT_EVENT);
        StreamRecord streamRecord1 = new StreamRecord();
        streamRecord1.setNewImage(createTestAttributeMap());
        record1.setDynamodb(streamRecord1);
        
        DynamodbEvent.DynamodbStreamRecord record2 = new DynamodbEvent.DynamodbStreamRecord();
        record2.setEventName(TestConstants.DynamoDb.INSERT_EVENT);
        StreamRecord streamRecord2 = new StreamRecord();
        streamRecord2.setNewImage(createTestAttributeMap());
        record2.setDynamodb(streamRecord2);
        
        event.setRecords(java.util.Arrays.asList(record1, record2));
        return event;
    }

    /**
     * Helper method to create mixed event types.
     */
    private DynamodbEvent createMixedEvents() {
        DynamodbEvent event = new DynamodbEvent();
        
        DynamodbEvent.DynamodbStreamRecord insertRecord = new DynamodbEvent.DynamodbStreamRecord();
        insertRecord.setEventName(TestConstants.DynamoDb.INSERT_EVENT);
        StreamRecord insertStreamRecord = new StreamRecord();
        insertStreamRecord.setNewImage(createTestAttributeMap());
        insertRecord.setDynamodb(insertStreamRecord);
        
        DynamodbEvent.DynamodbStreamRecord modifyRecord = new DynamodbEvent.DynamodbStreamRecord();
        modifyRecord.setEventName(TestConstants.DynamoDb.MODIFY_EVENT);
        StreamRecord modifyStreamRecord = new StreamRecord();
        modifyStreamRecord.setNewImage(createTestAttributeMap());
        modifyRecord.setDynamodb(modifyStreamRecord);
        
        DynamodbEvent.DynamodbStreamRecord removeRecord = new DynamodbEvent.DynamodbStreamRecord();
        removeRecord.setEventName(TestConstants.DynamoDb.REMOVE_EVENT);
        StreamRecord removeStreamRecord = new StreamRecord();
        removeStreamRecord.setNewImage(createTestAttributeMap());
        removeRecord.setDynamodb(removeStreamRecord);
        
        event.setRecords(java.util.Arrays.asList(insertRecord, modifyRecord, removeRecord));
        return event;
    }

    /**
     * Helper method to create test AttributeValue map for DynamoDB records.
     */
    private Map<String, AttributeValue> createTestAttributeMap() {
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue idValue = new AttributeValue();
        idValue.setS(TestConstants.Messages.TEST_MESSAGE_ID);
        attributeMap.put(TestConstants.DynamoDb.ID_ATTRIBUTE, idValue);
        
        AttributeValue usernameValue = new AttributeValue();
        usernameValue.setS(TestConstants.Messages.TEST_USERNAME);
        attributeMap.put(TestConstants.DynamoDb.USERNAME_ATTRIBUTE, usernameValue);
        
        AttributeValue platformValue = new AttributeValue();
        platformValue.setS(TestConstants.Messages.GMAIL_PLATFORM);
        attributeMap.put(TestConstants.DynamoDb.PLATFORM_ATTRIBUTE, platformValue);
        
        return attributeMap;
    }

    /**
     * Helper method to create a test Message object.
     */
    private Message createTestMessage() {
        return Message.builder()
                .id(TestConstants.Messages.TEST_MESSAGE_ID)
                .username(TestConstants.Messages.TEST_USERNAME)
                .platform(TestConstants.Messages.GMAIL_PLATFORM)
                .platformMessageId(TestConstants.Messages.GMAIL_PLATFORM_ID)
                .recipient(TestConstants.Messages.TEST_EMAIL)
                .sender(TestConstants.Messages.SENDER_EMAIL)
                .receivedAt(System.currentTimeMillis())
                .subject(TestData.TEST_SUBJECT_SIMPLE)
                .body(TestData.TEST_MESSAGE_BODY_SIMPLE)
                .build();
    }
}
