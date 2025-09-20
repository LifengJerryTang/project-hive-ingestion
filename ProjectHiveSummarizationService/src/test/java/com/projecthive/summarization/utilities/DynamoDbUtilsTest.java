package com.projecthive.summarization.utilities;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.projecthive.summarization.constants.TestConstants.DynamoDb;
import com.projecthive.summarization.constants.TestConstants.DynamoDbTestData;
import com.projecthive.summarization.constants.TestConstants.Numbers;
import com.projecthive.summarization.constants.TestConstants.SpecialData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DynamoDbUtils to ensure 100% code coverage.
 * Tests AttributeValue conversion between Lambda Events and AWS SDK v2 formats.
 * 
 * Note: This class is being maintained for backward compatibility but the new
 * StreamRecordConverter is the recommended approach for DynamoDB Stream processing.
 */
@ExtendWith(MockitoExtension.class)
class DynamoDbUtilsTest {

    @Test
    void convertAttributeMap_withStringValues_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue stringValue = new AttributeValue();
        stringValue.setS(DynamoDbTestData.TEST_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.STRING_FIELD, stringValue);
        
        AttributeValue emptyStringValue = new AttributeValue();
        emptyStringValue.setS(SpecialData.EMPTY_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.EMPTY_STRING_FIELD, emptyStringValue);
        
        AttributeValue specialCharsValue = new AttributeValue();
        specialCharsValue.setS(DynamoDbTestData.SPECIAL_CHARS_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.SPECIAL_CHARS_FIELD, specialCharsValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(3, result.size());
        assertEquals(DynamoDbTestData.TEST_STRING, result.get(DynamoDbTestData.STRING_FIELD).s());
        assertEquals(SpecialData.EMPTY_STRING, result.get(DynamoDbTestData.EMPTY_STRING_FIELD).s());
        assertEquals(DynamoDbTestData.SPECIAL_CHARS_STRING, result.get(DynamoDbTestData.SPECIAL_CHARS_FIELD).s());
    }

    @Test
    void convertAttributeMap_withNumericValues_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue intValue = new AttributeValue();
        intValue.setN(Numbers.NUMBER_42);
        lambdaAttributeMap.put(DynamoDbTestData.INT_FIELD, intValue);
        
        AttributeValue longValue = new AttributeValue();
        longValue.setN(Numbers.LONG_TIMESTAMP);
        lambdaAttributeMap.put(DynamoDbTestData.LONG_FIELD, longValue);
        
        AttributeValue floatValue = new AttributeValue();
        floatValue.setN(Numbers.FLOAT_PI);
        lambdaAttributeMap.put(DynamoDbTestData.FLOAT_FIELD, floatValue);
        
        AttributeValue negativeValue = new AttributeValue();
        negativeValue.setN(Numbers.NEGATIVE_123);
        lambdaAttributeMap.put(DynamoDbTestData.NEGATIVE_FIELD, negativeValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(4, result.size());
        assertEquals(Numbers.NUMBER_42, result.get(DynamoDbTestData.INT_FIELD).n());
        assertEquals(Numbers.LONG_TIMESTAMP, result.get(DynamoDbTestData.LONG_FIELD).n());
        assertEquals(Numbers.FLOAT_PI, result.get(DynamoDbTestData.FLOAT_FIELD).n());
        assertEquals(Numbers.NEGATIVE_123, result.get(DynamoDbTestData.NEGATIVE_FIELD).n());
    }

    @Test
    void convertAttributeMap_withBooleanValues_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue trueValue = new AttributeValue();
        trueValue.setBOOL(true);
        lambdaAttributeMap.put(DynamoDbTestData.TRUE_FIELD, trueValue);
        
        AttributeValue falseValue = new AttributeValue();
        falseValue.setBOOL(false);
        lambdaAttributeMap.put(DynamoDbTestData.FALSE_FIELD, falseValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(DynamoDbTestData.TRUE_FIELD).bool());
        assertFalse(result.get(DynamoDbTestData.FALSE_FIELD).bool());
    }

    @Test
    void convertAttributeMap_withNullValues_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue nullValue = new AttributeValue();
        nullValue.setNULL(true);
        lambdaAttributeMap.put(DynamoDbTestData.NULL_FIELD, nullValue);
        
        AttributeValue anotherNullValue = new AttributeValue();
        anotherNullValue.setNULL(false); // This should still be treated as null
        lambdaAttributeMap.put(DynamoDbTestData.ANOTHER_NULL_FIELD, anotherNullValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(DynamoDbTestData.NULL_FIELD).nul());
        assertFalse(result.get(DynamoDbTestData.ANOTHER_NULL_FIELD).nul());
    }

    @Test
    void convertAttributeMap_withStringSet_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue stringSetValue = new AttributeValue();
        stringSetValue.setSS(Arrays.asList(Numbers.STRING_SET_VALUES));
        lambdaAttributeMap.put(DynamoDbTestData.STRING_SET_FIELD, stringSetValue);
        
        AttributeValue emptyStringSetValue = new AttributeValue();
        emptyStringSetValue.setSS(Collections.emptyList());
        lambdaAttributeMap.put(DynamoDbTestData.EMPTY_STRING_SET_FIELD, emptyStringSetValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(2, result.size());
        assertEquals(Arrays.asList(Numbers.STRING_SET_VALUES), result.get(DynamoDbTestData.STRING_SET_FIELD).ss());
        assertEquals(Collections.emptyList(), result.get(DynamoDbTestData.EMPTY_STRING_SET_FIELD).ss());
    }

    @Test
    void convertAttributeMap_withNumberSet_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue numberSetValue = new AttributeValue();
        numberSetValue.setNS(Arrays.asList(Numbers.NUMBER_SET_VALUES));
        lambdaAttributeMap.put(DynamoDbTestData.NUMBER_SET_FIELD, numberSetValue);
        
        AttributeValue emptyNumberSetValue = new AttributeValue();
        emptyNumberSetValue.setNS(Collections.emptyList());
        lambdaAttributeMap.put(DynamoDbTestData.EMPTY_NUMBER_SET_FIELD, emptyNumberSetValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(2, result.size());
        assertEquals(Arrays.asList(Numbers.NUMBER_SET_VALUES), result.get(DynamoDbTestData.NUMBER_SET_FIELD).ns());
        assertEquals(Collections.emptyList(), result.get(DynamoDbTestData.EMPTY_NUMBER_SET_FIELD).ns());
    }

    @Test
    void convertAttributeMap_withBinaryData_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        byte[] binaryData = DynamoDbTestData.TEST_STRING.getBytes();
        AttributeValue binaryValue = new AttributeValue();
        binaryValue.setB(ByteBuffer.wrap(binaryData));
        lambdaAttributeMap.put(DynamoDbTestData.BINARY_FIELD, binaryValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(1, result.size());
        SdkBytes resultBytes = result.get(DynamoDbTestData.BINARY_FIELD).b();
        assertArrayEquals(binaryData, resultBytes.asByteArray());
    }

    @Test
    void convertAttributeMap_withBinarySet_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        List<ByteBuffer> binarySet = Arrays.asList(
            ByteBuffer.wrap(DynamoDbTestData.NESTED_STRING_VALUE.getBytes()),
            ByteBuffer.wrap(DynamoDbTestData.OUTER_STRING_VALUE.getBytes()),
            ByteBuffer.wrap(DynamoDbTestData.INNER_STRING_VALUE.getBytes())
        );
        
        AttributeValue binarySetValue = new AttributeValue();
        binarySetValue.setBS(binarySet);
        lambdaAttributeMap.put(DynamoDbTestData.BINARY_SET_FIELD, binarySetValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(1, result.size());
        List<SdkBytes> resultBinarySet = result.get(DynamoDbTestData.BINARY_SET_FIELD).bs();
        assertEquals(3, resultBinarySet.size());
        assertArrayEquals(DynamoDbTestData.NESTED_STRING_VALUE.getBytes(), resultBinarySet.get(0).asByteArray());
        assertArrayEquals(DynamoDbTestData.OUTER_STRING_VALUE.getBytes(), resultBinarySet.get(1).asByteArray());
        assertArrayEquals(DynamoDbTestData.INNER_STRING_VALUE.getBytes(), resultBinarySet.get(2).asByteArray());
    }

    @Test
    void convertAttributeMap_withNestedMap_shouldConvertRecursively() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        // Create nested map
        Map<String, AttributeValue> nestedMap = new HashMap<>();
        
        AttributeValue nestedStringValue = new AttributeValue();
        nestedStringValue.setS(DynamoDbTestData.NESTED_STRING_VALUE);
        nestedMap.put(DynamoDbTestData.NESTED_STRING_FIELD, nestedStringValue);
        
        AttributeValue nestedNumberValue = new AttributeValue();
        nestedNumberValue.setN(Numbers.NUMBER_123);
        nestedMap.put(DynamoDbTestData.NESTED_NUMBER_FIELD, nestedNumberValue);
        
        AttributeValue nestedBoolValue = new AttributeValue();
        nestedBoolValue.setBOOL(true);
        nestedMap.put(DynamoDbTestData.NESTED_BOOL_FIELD, nestedBoolValue);
        
        AttributeValue mapValue = new AttributeValue();
        mapValue.setM(nestedMap);
        lambdaAttributeMap.put(DynamoDbTestData.MAP_FIELD, mapValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(1, result.size());
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> resultMap = 
            result.get(DynamoDbTestData.MAP_FIELD).m();
        assertEquals(3, resultMap.size());
        assertEquals(DynamoDbTestData.NESTED_STRING_VALUE, resultMap.get(DynamoDbTestData.NESTED_STRING_FIELD).s());
        assertEquals(Numbers.NUMBER_123, resultMap.get(DynamoDbTestData.NESTED_NUMBER_FIELD).n());
        assertTrue(resultMap.get(DynamoDbTestData.NESTED_BOOL_FIELD).bool());
    }

    @Test
    void convertAttributeMap_withList_shouldConvertRecursively() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        // Create list of different types
        List<AttributeValue> listValues = new ArrayList<>();
        
        AttributeValue listStringValue = new AttributeValue();
        listStringValue.setS(DynamoDbTestData.LIST_STRING_VALUE);
        listValues.add(listStringValue);
        
        AttributeValue listNumberValue = new AttributeValue();
        listNumberValue.setN(Numbers.NUMBER_456);
        listValues.add(listNumberValue);
        
        AttributeValue listBoolValue = new AttributeValue();
        listBoolValue.setBOOL(false);
        listValues.add(listBoolValue);
        
        AttributeValue listNullValue = new AttributeValue();
        listNullValue.setNULL(true);
        listValues.add(listNullValue);
        
        AttributeValue listValue = new AttributeValue();
        listValue.setL(listValues);
        lambdaAttributeMap.put(DynamoDbTestData.LIST_FIELD, listValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(1, result.size());
        List<software.amazon.awssdk.services.dynamodb.model.AttributeValue> resultList = 
            result.get(DynamoDbTestData.LIST_FIELD).l();
        assertEquals(4, resultList.size());
        assertEquals(DynamoDbTestData.LIST_STRING_VALUE, resultList.get(0).s());
        assertEquals(Numbers.NUMBER_456, resultList.get(1).n());
        assertFalse(resultList.get(2).bool());
        assertTrue(resultList.get(3).nul());
    }

    @Test
    void convertAttributeMap_withComplexNestedStructure_shouldConvertCorrectly() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        // Create complex nested structure: Map containing List containing Map
        Map<String, AttributeValue> outerMap = new HashMap<>();
        
        // Nested list within the outer map
        List<AttributeValue> nestedList = new ArrayList<>();
        
        // Map within the list
        Map<String, AttributeValue> innerMap = new HashMap<>();
        AttributeValue innerStringValue = new AttributeValue();
        innerStringValue.setS(DynamoDbTestData.INNER_STRING_VALUE);
        innerMap.put(DynamoDbTestData.INNER_STRING_FIELD, innerStringValue);
        
        AttributeValue innerMapValue = new AttributeValue();
        innerMapValue.setM(innerMap);
        nestedList.add(innerMapValue);
        
        // String within the list
        AttributeValue listStringValue = new AttributeValue();
        listStringValue.setS(DynamoDbTestData.LIST_ITEM_VALUE);
        nestedList.add(listStringValue);
        
        AttributeValue nestedListValue = new AttributeValue();
        nestedListValue.setL(nestedList);
        outerMap.put(DynamoDbTestData.NESTED_LIST_FIELD, nestedListValue);
        
        // Simple string in outer map
        AttributeValue outerStringValue = new AttributeValue();
        outerStringValue.setS(DynamoDbTestData.OUTER_STRING_VALUE);
        outerMap.put(DynamoDbTestData.OUTER_STRING_FIELD, outerStringValue);
        
        AttributeValue complexMapValue = new AttributeValue();
        complexMapValue.setM(outerMap);
        lambdaAttributeMap.put(DynamoDbTestData.COMPLEX_FIELD, complexMapValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(1, result.size());
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> resultComplexMap = 
            result.get(DynamoDbTestData.COMPLEX_FIELD).m();
        assertEquals(2, resultComplexMap.size());
        assertEquals(DynamoDbTestData.OUTER_STRING_VALUE, resultComplexMap.get(DynamoDbTestData.OUTER_STRING_FIELD).s());
        
        List<software.amazon.awssdk.services.dynamodb.model.AttributeValue> resultNestedList = 
            resultComplexMap.get(DynamoDbTestData.NESTED_LIST_FIELD).l();
        assertEquals(2, resultNestedList.size());
        
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> resultInnerMap = 
            resultNestedList.get(0).m();
        assertEquals(DynamoDbTestData.INNER_STRING_VALUE, resultInnerMap.get(DynamoDbTestData.INNER_STRING_FIELD).s());
        assertEquals(DynamoDbTestData.LIST_ITEM_VALUE, resultNestedList.get(1).s());
    }

    @Test
    void convertAttributeMap_withEmptyCollections_shouldHandleEmptyValues() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        // Empty map
        AttributeValue emptyMapValue = new AttributeValue();
        emptyMapValue.setM(Collections.emptyMap());
        lambdaAttributeMap.put(DynamoDbTestData.EMPTY_MAP_FIELD, emptyMapValue);
        
        // Empty list
        AttributeValue emptyListValue = new AttributeValue();
        emptyListValue.setL(Collections.emptyList());
        lambdaAttributeMap.put(DynamoDbTestData.EMPTY_LIST_FIELD, emptyListValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(DynamoDbTestData.EMPTY_MAP_FIELD).m().isEmpty());
        assertTrue(result.get(DynamoDbTestData.EMPTY_LIST_FIELD).l().isEmpty());
    }

    @Test
    void convertAttributeMap_withMixedDataTypes_shouldConvertAllTypes() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        // String
        AttributeValue stringValue = new AttributeValue();
        stringValue.setS(DynamoDbTestData.MIXED_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.STRING_FIELD, stringValue);
        
        // Number
        AttributeValue numberValue = new AttributeValue();
        numberValue.setN(Numbers.NUMBER_789);
        lambdaAttributeMap.put(DynamoDbTestData.NUMBER_FIELD, numberValue);
        
        // Boolean
        AttributeValue boolValue = new AttributeValue();
        boolValue.setBOOL(true);
        lambdaAttributeMap.put(DynamoDbTestData.BOOL_FIELD, boolValue);
        
        // Null
        AttributeValue nullValue = new AttributeValue();
        nullValue.setNULL(true);
        lambdaAttributeMap.put(DynamoDbTestData.NULL_FIELD, nullValue);
        
        // String Set
        AttributeValue stringSetValue = new AttributeValue();
        stringSetValue.setSS(Arrays.asList(Numbers.STRING_SET_SIMPLE));
        lambdaAttributeMap.put(DynamoDbTestData.STRING_SET_FIELD, stringSetValue);
        
        // Number Set
        AttributeValue numberSetValue = new AttributeValue();
        numberSetValue.setNS(Arrays.asList(Numbers.NUMBER_SET_SIMPLE));
        lambdaAttributeMap.put(DynamoDbTestData.NUMBER_SET_FIELD, numberSetValue);
        
        // Binary
        AttributeValue binaryValue = new AttributeValue();
        binaryValue.setB(ByteBuffer.wrap(DynamoDbTestData.TEST_STRING.getBytes()));
        lambdaAttributeMap.put(DynamoDbTestData.BINARY_FIELD, binaryValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(7, result.size());
        assertEquals(DynamoDbTestData.MIXED_STRING, result.get(DynamoDbTestData.STRING_FIELD).s());
        assertEquals(Numbers.NUMBER_789, result.get(DynamoDbTestData.NUMBER_FIELD).n());
        assertTrue(result.get(DynamoDbTestData.BOOL_FIELD).bool());
        assertTrue(result.get(DynamoDbTestData.NULL_FIELD).nul());
        assertEquals(Arrays.asList(Numbers.STRING_SET_SIMPLE), result.get(DynamoDbTestData.STRING_SET_FIELD).ss());
        assertEquals(Arrays.asList(Numbers.NUMBER_SET_SIMPLE), result.get(DynamoDbTestData.NUMBER_SET_FIELD).ns());
        assertArrayEquals(DynamoDbTestData.TEST_STRING.getBytes(), result.get(DynamoDbTestData.BINARY_FIELD).b().asByteArray());
    }

    @Test
    void convertAttributeMap_withEmptyInput_shouldReturnEmptyMap() {
        // Arrange
        Map<String, AttributeValue> emptyLambdaAttributeMap = new HashMap<>();

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(emptyLambdaAttributeMap);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void convertAttributeMap_withUnicodeAndSpecialCharacters_shouldPreserveCharacters() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue unicodeValue = new AttributeValue();
        unicodeValue.setS(SpecialData.UNICODE_TEST);
        lambdaAttributeMap.put(DynamoDbTestData.UNICODE_FIELD, unicodeValue);
        
        AttributeValue jsonLikeValue = new AttributeValue();
        jsonLikeValue.setS(SpecialData.JSON_LIKE_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.JSON_FIELD, jsonLikeValue);
        
        AttributeValue newlineValue = new AttributeValue();
        newlineValue.setS(SpecialData.NEWLINE_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.NEWLINE_FIELD, newlineValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(3, result.size());
        assertEquals(SpecialData.UNICODE_TEST, result.get(DynamoDbTestData.UNICODE_FIELD).s());
        assertEquals(SpecialData.JSON_LIKE_STRING, result.get(DynamoDbTestData.JSON_FIELD).s());
        assertEquals(SpecialData.NEWLINE_STRING, result.get(DynamoDbTestData.NEWLINE_FIELD).s());
    }

    @Test
    void convertAttributeMap_withLargeNumbers_shouldHandleLargeValues() {
        // Arrange
        Map<String, AttributeValue> lambdaAttributeMap = new HashMap<>();
        
        AttributeValue largeIntValue = new AttributeValue();
        largeIntValue.setN(Numbers.LARGE_INT_STRING); // Long.MAX_VALUE
        lambdaAttributeMap.put(DynamoDbTestData.LARGE_INT_FIELD, largeIntValue);
        
        AttributeValue largeFloatValue = new AttributeValue();
        largeFloatValue.setN(Numbers.LARGE_FLOAT_STRING); // Close to Double.MAX_VALUE
        lambdaAttributeMap.put(DynamoDbTestData.LARGE_FLOAT_FIELD, largeFloatValue);
        
        AttributeValue precisionFloatValue = new AttributeValue();
        precisionFloatValue.setN(Numbers.PRECISION_FLOAT_STRING);
        lambdaAttributeMap.put(DynamoDbTestData.PRECISION_FLOAT_FIELD, precisionFloatValue);

        // Act
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> result = 
            DynamoDbUtils.convertAttributeMap(lambdaAttributeMap);

        // Assert
        assertEquals(3, result.size());
        assertEquals(Numbers.LARGE_INT_STRING, result.get(DynamoDbTestData.LARGE_INT_FIELD).n());
        assertEquals(Numbers.LARGE_FLOAT_STRING, result.get(DynamoDbTestData.LARGE_FLOAT_FIELD).n());
        assertEquals(Numbers.PRECISION_FLOAT_STRING, result.get(DynamoDbTestData.PRECISION_FLOAT_FIELD).n());
    }
}
