package com.projecthive.summarization.models;

import lombok.*;
import java.util.Map;

// AWS Enhanced DynamoDB Client annotations for automatic object mapping
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * Represents a message from various platforms (Gmail, Discord, etc.) that will be processed
 * for AI summarization. This class is configured for AWS DynamoDB Enhanced Client to enable
 * automatic conversion between DynamoDB records and Java objects.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean // Marks this class as a DynamoDB entity for Enhanced Client auto-mapping
public class Message {

    @NonNull
    private String id; // Internal UUID

    /**
     * Returns the unique identifier for this message.
     * Annotated as partition key for DynamoDB Enhanced Client integration.
     * This enables automatic conversion from DynamoDB Stream records to Message objects.
     */
    @DynamoDbPartitionKey // Designates this field as the DynamoDB table's primary key
    public String getId() {
        return id;
    }

    @NonNull
    private String username;

    @NonNull
    private String platform; // "gmail", "discord", etc.

    @NonNull
    private String platformMessageId; // Gmail message ID, Discord message ID, etc.

    @NonNull
    private String recipient; // "to" for email, channel ID for Discord/Slack

    @NonNull
    private String sender;

    @NonNull
    private Long receivedAt; // Epoch millis

    private String subject;

    private String body;

    private Map<String, String> metadata;

}
