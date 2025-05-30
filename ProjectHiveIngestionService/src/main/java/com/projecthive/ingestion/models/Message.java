package com.projecthive.ingestion.models;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class Message {

    @NonNull
    private String id; // Internal UUID

    @NonNull
    private String platform; // "gmail", "discord", etc.

    @NonNull
    private String platformMessageId; // Gmail message ID, Discord message ID, etc.

    @NonNull
    private String recipient; // "to" for email, channel ID for Discord/Slack

    @NonNull
    private String sender;

    private String subject;

    private String body;

    @NonNull
    private Long receivedAt; // Epoch millis

    private Map<String, String> metadata;

    @DynamoDbPartitionKey
    public @NonNull String getId() {
        return id;
    }
}
