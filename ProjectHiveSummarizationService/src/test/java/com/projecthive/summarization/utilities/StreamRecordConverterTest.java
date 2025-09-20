package com.projecthive.summarization.utilities;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.projecthive.summarization.constants.TestConstants.StreamConverter;
import com.projecthive.summarization.constants.TestConstants.Metadata;
import com.projecthive.summarization.constants.TestConstants.Numbers;
import com.projecthive.summarization.constants.TestConstants.TestData;
import com.projecthive.summarization.models.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StreamRecordConverter to ensure 100% code coverage.
 * Tests DynamoDB Stream record conversion, AttributeValue handling, and error scenarios.
 */
@ExtendWith(MockitoExtension.class)
class StreamRecordConverterTest {

    @Test
    void convertToMessage_withCompleteRecord_shouldConvertSuccessfully() {
        // Arrange
        DynamodbEvent.DynamodbStreamRecord record = createCompleteStreamRecord();

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
        assertEquals(StreamConverter.COMPLETE_TEST_MESSAGE_ID, result.getId());
        assertEquals(StreamConverter.COMPLETE_TEST_USERNAME, result.getUsername());
        assertEquals(StreamConverter.COMPLETE_TEST_PLATFORM, result.getPlatform());
        assertEquals(StreamConverter.COMPLETE_PLATFORM_MESSAGE_ID, result.getPlatformMessageId());
        assertEquals(StreamConverter.COMPLETE_RECIPIENT, result.getRecipient());
        assertEquals(StreamConverter.COMPLETE_SENDER, result.getSender());
        assertEquals(StreamConverter.COMPLETE_RECEIVED_AT, result.getReceivedAt());
        assertEquals(StreamConverter.COMPLETE_SUBJECT, result.getSubject());
        assertEquals(StreamConverter.COMPLETE_BODY, result.getBody());
        assertNotNull(result.getMetadata());
        assertEquals(Metadata.PRIORITY_HIGH, result.getMetadata().get(Metadata.PRIORITY_KEY));
        assertEquals(Metadata.CATEGORY_URGENT, result.getMetadata().get(Metadata.CATEGORY_KEY));
    }

    @Test
    void convertToMessage_withMinimalRecord_shouldHandleRequiredFieldsOnly() {
        // Arrange
        DynamodbEvent.DynamodbStreamRecord record = createMinimalStreamRecord();

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
        assertEquals(StreamConverter.MINIMAL_ID, result.getId());
        assertEquals(StreamConverter.MINIMAL_USERNAME, result.getUsername());
        assertEquals(StreamConverter.MINIMAL_PLATFORM, result.getPlatform());
        assertEquals(StreamConverter.MINIMAL_PLATFORM_MESSAGE_ID, result.getPlatformMessageId());
        assertEquals(StreamConverter.MINIMAL_RECIPIENT, result.getRecipient());
        assertEquals(StreamConverter.MINIMAL_SENDER, result.getSender());
        assertEquals(StreamConverter.MINIMAL_RECEIVED_AT, result.getReceivedAt());
        assertNull(result.getSubject()); // Discord messages may not have subjects
        assertEquals(StreamConverter.MINIMAL_BODY, result.getBody());
        assertNull(result.getMetadata()); // No metadata
    }

    @Test
    void convertToMessage_withStringValues_shouldHandleAllStringTypes() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue stringValue = new AttributeValue();
        stringValue.setS(StreamConverter.STRING_VALUE);
        attributeMap.put(StreamConverter.ID_FIELD, stringValue);
        
        AttributeValue emptyStringValue = new AttributeValue();
        emptyStringValue.setS("");
        attributeMap.put(StreamConverter.USERNAME_FIELD, emptyStringValue);
        
        AttributeValue specialCharsValue = new AttributeValue();
        specialCharsValue.setS(StreamConverter.SPECIAL_CHARS_PLATFORM);
        attributeMap.put(StreamConverter.PLATFORM_FIELD, specialCharsValue);
        
        AttributeValue unicodeValue = new AttributeValue();
        unicodeValue.setS(StreamConverter.UNICODE_PLATFORM_ID);
        attributeMap.put(StreamConverter.PLATFORM_MESSAGE_ID_FIELD, unicodeValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertEquals(StreamConverter.STRING_VALUE, result.getId());
        assertEquals("", result.getUsername());
        assertEquals(StreamConverter.SPECIAL_CHARS_PLATFORM, result.getPlatform());
        assertEquals(StreamConverter.UNICODE_PLATFORM_ID, result.getPlatformMessageId());
    }

    @Test
    void convertToMessage_withNumericValues_shouldHandleNumbers() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue longValue = new AttributeValue();
        longValue.setN(StreamConverter.LONG_TIMESTAMP_STRING);
        attributeMap.put(StreamConverter.RECEIVED_AT_FIELD, longValue);
        
        AttributeValue intValue = new AttributeValue();
        intValue.setN(Numbers.NUMBER_42);
        attributeMap.put("messageCount", intValue);
        
        AttributeValue floatValue = new AttributeValue();
        floatValue.setN(Numbers.FLOAT_PI);
        attributeMap.put("priority", floatValue);
        
        // Add required string fields
        addRequiredStringFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertEquals(StreamConverter.COMPLETE_RECEIVED_AT, result.getReceivedAt());
    }

    @Test
    void convertToMessage_withBooleanValues_shouldHandleBooleans() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue trueValue = new AttributeValue();
        trueValue.setBOOL(true);
        attributeMap.put("isRead", trueValue);
        
        AttributeValue falseValue = new AttributeValue();
        falseValue.setBOOL(false);
        attributeMap.put("isArchived", falseValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert - Boolean values are handled by the converter
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withNullValues_shouldHandleNulls() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue nullValue = new AttributeValue();
        nullValue.setNULL(true);
        attributeMap.put("subject", nullValue);
        
        AttributeValue anotherNullValue = new AttributeValue();
        anotherNullValue.setNULL(true);
        attributeMap.put("metadata", anotherNullValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
        assertNull(result.getSubject());
        assertNull(result.getMetadata());
    }

    @Test
    void convertToMessage_withStringSet_shouldHandleStringSets() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue stringSetValue = new AttributeValue();
        stringSetValue.setSS(Arrays.asList(StreamConverter.TAG_VALUES));
        attributeMap.put("tags", stringSetValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withNumberSet_shouldHandleNumberSets() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        AttributeValue numberSetValue = new AttributeValue();
        numberSetValue.setNS(Arrays.asList(StreamConverter.SCORE_VALUES));
        attributeMap.put("scores", numberSetValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withMapValues_shouldHandleNestedMaps() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // Create nested map for metadata
        Map<String, AttributeValue> metadataMap = new HashMap<>();
        AttributeValue priorityValue = new AttributeValue();
        priorityValue.setS("high");
        metadataMap.put("priority", priorityValue);
        
        AttributeValue categoryValue = new AttributeValue();
        categoryValue.setS("urgent");
        metadataMap.put("category", categoryValue);
        
        AttributeValue countValue = new AttributeValue();
        countValue.setN("5");
        metadataMap.put("count", countValue);
        
        AttributeValue mapValue = new AttributeValue();
        mapValue.setM(metadataMap);
        attributeMap.put("metadata", mapValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getMetadata());
        assertEquals("high", result.getMetadata().get("priority"));
        assertEquals("urgent", result.getMetadata().get("category"));
    }

    @Test
    void convertToMessage_withListValues_shouldHandleLists() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // Create list of AttributeValues
        List<AttributeValue> listValues = new ArrayList<>();
        
        AttributeValue listItem1 = new AttributeValue();
        listItem1.setS("item1");
        listValues.add(listItem1);
        
        AttributeValue listItem2 = new AttributeValue();
        listItem2.setN("42");
        listValues.add(listItem2);
        
        AttributeValue listItem3 = new AttributeValue();
        listItem3.setBOOL(true);
        listValues.add(listItem3);
        
        AttributeValue listValue = new AttributeValue();
        listValue.setL(listValues);
        attributeMap.put("attachments", listValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withBinaryData_shouldHandleBinaryValues() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        byte[] binaryData = StreamConverter.BINARY_CONTENT.getBytes();
        AttributeValue binaryValue = new AttributeValue();
        binaryValue.setB(ByteBuffer.wrap(binaryData));
        attributeMap.put("attachment", binaryValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withBinarySet_shouldHandleBinarySets() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        List<ByteBuffer> binarySet = Arrays.asList(
            ByteBuffer.wrap(StreamConverter.BINARY_1_CONTENT.getBytes()),
            ByteBuffer.wrap(StreamConverter.BINARY_2_CONTENT.getBytes())
        );
        
        AttributeValue binarySetValue = new AttributeValue();
        binarySetValue.setBS(binarySet);
        attributeMap.put("attachments", binarySetValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withUnsupportedAttributeType_shouldThrowException() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // Create an AttributeValue with no type set (unsupported)
        AttributeValue unsupportedValue = new AttributeValue();
        // Don't set any type - this should cause an exception
        attributeMap.put("unsupported", unsupportedValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            StreamRecordConverter.convertToMessage(record);
        });
        
        assertTrue(exception.getMessage().contains(StreamConverter.UNSUPPORTED_ATTRIBUTE_ERROR));
    }

    @Test
    void convertToMessage_withComplexNestedStructure_shouldHandleComplexity() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // Create complex nested structure
        Map<String, AttributeValue> nestedMap = new HashMap<>();
        
        // Nested list within map
        List<AttributeValue> nestedList = new ArrayList<>();
        AttributeValue nestedItem1 = new AttributeValue();
        nestedItem1.setS("nested-string");
        nestedList.add(nestedItem1);
        
        AttributeValue nestedItem2 = new AttributeValue();
        nestedItem2.setN("123");
        nestedList.add(nestedItem2);
        
        AttributeValue nestedListValue = new AttributeValue();
        nestedListValue.setL(nestedList);
        nestedMap.put("nestedList", nestedListValue);
        
        // Nested string in map
        AttributeValue nestedStringValue = new AttributeValue();
        nestedStringValue.setS("nested-value");
        nestedMap.put("nestedString", nestedStringValue);
        
        AttributeValue complexMapValue = new AttributeValue();
        complexMapValue.setM(nestedMap);
        attributeMap.put("complexData", complexMapValue);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    @Test
    void convertToMessage_withEmptyCollections_shouldHandleEmptyValues() {
        // Arrange
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // Empty string set
        AttributeValue emptyStringSet = new AttributeValue();
        emptyStringSet.setSS(Collections.emptyList());
        attributeMap.put("emptyTags", emptyStringSet);
        
        // Empty number set
        AttributeValue emptyNumberSet = new AttributeValue();
        emptyNumberSet.setNS(Collections.emptyList());
        attributeMap.put("emptyScores", emptyNumberSet);
        
        // Empty list
        AttributeValue emptyList = new AttributeValue();
        emptyList.setL(Collections.emptyList());
        attributeMap.put("emptyAttachments", emptyList);
        
        // Empty map
        AttributeValue emptyMap = new AttributeValue();
        emptyMap.setM(Collections.emptyMap());
        attributeMap.put("emptyMetadata", emptyMap);
        
        // Add required fields
        addRequiredFields(attributeMap);
        
        DynamodbEvent.DynamodbStreamRecord record = createRecordWithAttributes(attributeMap);

        // Act
        Message result = StreamRecordConverter.convertToMessage(record);

        // Assert
        assertNotNull(result);
    }

    /**
     * Helper method to create a complete stream record with all field types.
     */
    private DynamodbEvent.DynamodbStreamRecord createCompleteStreamRecord() {
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        // String values
        addAttributeValue(attributeMap, StreamConverter.ID_FIELD, StreamConverter.COMPLETE_TEST_MESSAGE_ID);
        addAttributeValue(attributeMap, StreamConverter.USERNAME_FIELD, StreamConverter.COMPLETE_TEST_USERNAME);
        addAttributeValue(attributeMap, StreamConverter.PLATFORM_FIELD, StreamConverter.COMPLETE_TEST_PLATFORM);
        addAttributeValue(attributeMap, StreamConverter.PLATFORM_MESSAGE_ID_FIELD, StreamConverter.COMPLETE_PLATFORM_MESSAGE_ID);
        addAttributeValue(attributeMap, StreamConverter.RECIPIENT_FIELD, StreamConverter.COMPLETE_RECIPIENT);
        addAttributeValue(attributeMap, StreamConverter.SENDER_FIELD, StreamConverter.COMPLETE_SENDER);
        addAttributeValue(attributeMap, StreamConverter.SUBJECT_FIELD, StreamConverter.COMPLETE_SUBJECT);
        addAttributeValue(attributeMap, StreamConverter.BODY_FIELD, StreamConverter.COMPLETE_BODY);
        
        // Numeric value
        AttributeValue receivedAtValue = new AttributeValue();
        receivedAtValue.setN(StreamConverter.LONG_TIMESTAMP_STRING);
        attributeMap.put(StreamConverter.RECEIVED_AT_FIELD, receivedAtValue);
        
        // Map value (metadata)
        Map<String, AttributeValue> metadataMap = new HashMap<>();
        addAttributeValue(metadataMap, Metadata.PRIORITY_KEY, Metadata.PRIORITY_HIGH);
        addAttributeValue(metadataMap, Metadata.CATEGORY_KEY, Metadata.CATEGORY_URGENT);
        
        AttributeValue metadataValue = new AttributeValue();
        metadataValue.setM(metadataMap);
        attributeMap.put(StreamConverter.METADATA_FIELD, metadataValue);
        
        return createRecordWithAttributes(attributeMap);
    }

    /**
     * Helper method to create a minimal stream record with only required fields.
     */
    private DynamodbEvent.DynamodbStreamRecord createMinimalStreamRecord() {
        Map<String, AttributeValue> attributeMap = new HashMap<>();
        
        addAttributeValue(attributeMap, StreamConverter.ID_FIELD, StreamConverter.MINIMAL_ID);
        addAttributeValue(attributeMap, StreamConverter.USERNAME_FIELD, StreamConverter.MINIMAL_USERNAME);
        addAttributeValue(attributeMap, StreamConverter.PLATFORM_FIELD, StreamConverter.MINIMAL_PLATFORM);
        addAttributeValue(attributeMap, StreamConverter.PLATFORM_MESSAGE_ID_FIELD, StreamConverter.MINIMAL_PLATFORM_MESSAGE_ID);
        addAttributeValue(attributeMap, StreamConverter.RECIPIENT_FIELD, StreamConverter.MINIMAL_RECIPIENT);
        addAttributeValue(attributeMap, StreamConverter.SENDER_FIELD, StreamConverter.MINIMAL_SENDER);
        addAttributeValue(attributeMap, StreamConverter.BODY_FIELD, StreamConverter.MINIMAL_BODY);
        
        AttributeValue receivedAtValue = new AttributeValue();
        receivedAtValue.setN(String.valueOf(StreamConverter.MINIMAL_RECEIVED_AT));
        attributeMap.put(StreamConverter.RECEIVED_AT_FIELD, receivedAtValue);
        
        // Subject is null for Discord messages
        AttributeValue nullSubject = new AttributeValue();
        nullSubject.setNULL(true);
        attributeMap.put(StreamConverter.SUBJECT_FIELD, nullSubject);
        
        // No metadata
        AttributeValue nullMetadata = new AttributeValue();
        nullMetadata.setNULL(true);
        attributeMap.put(StreamConverter.METADATA_FIELD, nullMetadata);
        
        return createRecordWithAttributes(attributeMap);
    }

    /**
     * Helper method to create a stream record with given attributes.
     */
    private DynamodbEvent.DynamodbStreamRecord createRecordWithAttributes(Map<String, AttributeValue> attributeMap) {
        DynamodbEvent.DynamodbStreamRecord record = new DynamodbEvent.DynamodbStreamRecord();
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setNewImage(attributeMap);
        record.setDynamodb(streamRecord);
        return record;
    }

    /**
     * Helper method to add string AttributeValue to map.
     */
    private void addAttributeValue(Map<String, AttributeValue> map, String key, String value) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setS(value);
        map.put(key, attributeValue);
    }

    /**
     * Helper method to add required fields for Message object.
     */
    private void addRequiredFields(Map<String, AttributeValue> attributeMap) {
        addRequiredStringFields(attributeMap);
        
        AttributeValue receivedAtValue = new AttributeValue();
        receivedAtValue.setN(StreamConverter.LONG_TIMESTAMP_STRING);
        attributeMap.put(StreamConverter.RECEIVED_AT_FIELD, receivedAtValue);
    }

    /**
     * Helper method to add required string fields for Message object.
     */
    private void addRequiredStringFields(Map<String, AttributeValue> attributeMap) {
        if (!attributeMap.containsKey(StreamConverter.ID_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.ID_FIELD, TestData.TEST_ID_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.USERNAME_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.USERNAME_FIELD, TestData.TEST_ID_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.PLATFORM_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.PLATFORM_FIELD, TestData.TEST_PLATFORM_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.PLATFORM_MESSAGE_ID_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.PLATFORM_MESSAGE_ID_FIELD, TestData.TEST_PLATFORM_ID_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.RECIPIENT_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.RECIPIENT_FIELD, TestData.TEST_RECIPIENT_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.SENDER_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.SENDER_FIELD, TestData.TEST_SENDER_DEFAULT);
        }
        if (!attributeMap.containsKey(StreamConverter.BODY_FIELD)) {
            addAttributeValue(attributeMap, StreamConverter.BODY_FIELD, TestData.TEST_BODY_DEFAULT);
        }
    }
}
