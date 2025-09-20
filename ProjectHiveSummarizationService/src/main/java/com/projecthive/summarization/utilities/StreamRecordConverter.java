package com.projecthive.summarization.utilities;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.projecthive.summarization.models.Message;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for converting DynamoDB Stream records to Java objects using the Enhanced Client.
 * This replaces manual AttributeValue conversion with AWS SDK's built-in mapping capabilities,
 * reducing code complexity and maintenance overhead.
 * 
 * Technical Context: DynamoDB Streams use Lambda Events AttributeValue format, while
 * Enhanced Client expects AWS SDK v2 AttributeValue format. This converter bridges that gap.
 */
public class StreamRecordConverter {

    // Pre-built TableSchema for efficient Message object mapping
    // TableSchema.fromBean() analyzes the @DynamoDbBean annotations at runtime
    private static final TableSchema<Message> MESSAGE_SCHEMA = TableSchema.fromBean(Message.class);

    // Private constructor to prevent instantiation of utility class
    private StreamRecordConverter() {}

    /**
     * Converts a DynamoDB Stream record to a Message object using Enhanced Client mapping.
     * This approach leverages AWS SDK's built-in conversion logic instead of manual mapping.
     * 
     * @param record DynamoDB Stream record from Lambda event
     * @return Message object with all fields populated from the stream record
     * @throws IllegalArgumentException if record format is invalid
     * @throws RuntimeException if conversion fails due to data type mismatches
     */
    public static Message convertToMessage(DynamodbEvent.DynamodbStreamRecord record) {
        // Extract the new image (post-change data) from the stream record
        // This contains the complete item data after the DynamoDB operation
        Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> lambdaAttributes = 
            record.getDynamodb().getNewImage();
        
        // Convert Lambda Events AttributeValue format to AWS SDK v2 AttributeValue format
        // This is necessary because Enhanced Client expects SDK v2 format
        Map<String, AttributeValue> sdkAttributes = lambdaAttributes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> convertLambdaAttributeToSdk(entry.getValue())
            ));
        
        // Use Enhanced Client's TableSchema to automatically map AttributeValues to Message object
        // This eliminates manual field-by-field mapping and reduces error potential
        return MESSAGE_SCHEMA.mapToItem(sdkAttributes);
    }

    /**
     * Converts a single Lambda Events AttributeValue to AWS SDK v2 AttributeValue.
     * Handles the most common DynamoDB data types used in messaging applications.
     * 
     * @param lambdaAttr AttributeValue from Lambda Events (DynamoDB Streams format)
     * @return AttributeValue in AWS SDK v2 format for Enhanced Client compatibility
     */
    private static AttributeValue convertLambdaAttributeToSdk(
            com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue lambdaAttr) {
        
        AttributeValue.Builder builder = AttributeValue.builder();
        
        // Handle String values (most common for message content, IDs, usernames, etc.)
        if (lambdaAttr.getS() != null) {
            return builder.s(lambdaAttr.getS()).build();
        }
        
        // Handle Numeric values (timestamps, counts, message lengths, etc.)
        if (lambdaAttr.getN() != null) {
            return builder.n(lambdaAttr.getN()).build();
        }
        
        // Handle Boolean values (flags, status indicators, read/unread status)
        if (lambdaAttr.getBOOL() != null) {
            return builder.bool(lambdaAttr.getBOOL()).build();
        }
        
        // Handle NULL values (optional fields that weren't set, like subject for Discord messages)
        if (lambdaAttr.getNULL() != null) {
            return builder.nul(lambdaAttr.getNULL()).build();
        }
        
        // Handle String Sets (tags, categories, participant lists)
        if (lambdaAttr.getSS() != null) {
            return builder.ss(lambdaAttr.getSS()).build();
        }
        
        // Handle Number Sets (multiple numeric values like reaction counts)
        if (lambdaAttr.getNS() != null) {
            return builder.ns(lambdaAttr.getNS()).build();
        }
        
        // Handle Map values (nested objects like metadata, attachments, etc.)
        if (lambdaAttr.getM() != null) {
            Map<String, AttributeValue> convertedMap = lambdaAttr.getM().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> convertLambdaAttributeToSdk(entry.getValue())
                ));
            return builder.m(convertedMap).build();
        }
        
        // Handle List values (arrays of mixed types, like message attachments)
        if (lambdaAttr.getL() != null) {
            return builder.l(
                lambdaAttr.getL().stream()
                    .map(StreamRecordConverter::convertLambdaAttributeToSdk)
                    .collect(Collectors.toList())
            ).build();
        }
        
        // Handle Binary data (file attachments, encoded content)
        if (lambdaAttr.getB() != null) {
            return builder.b(software.amazon.awssdk.core.SdkBytes.fromByteBuffer(lambdaAttr.getB())).build();
        }
        
        // Handle Binary Sets (multiple binary values)
        if (lambdaAttr.getBS() != null) {
            return builder.bs(
                lambdaAttr.getBS().stream()
                    .map(software.amazon.awssdk.core.SdkBytes::fromByteBuffer)
                    .collect(Collectors.toList())
            ).build();
        }
        
        // Throw exception for unsupported types to fail fast and aid debugging
        // This helps identify new DynamoDB types that need handling
        throw new IllegalArgumentException("Unsupported AttributeValue type in stream record: " + lambdaAttr);
    }
}
