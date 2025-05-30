package com.projecthive.ingestion.models;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GmailMessage {

    @NonNull
    private String id;          // Gmail message ID

    @NonNull
    private String from;        // Email sender

    @NonNull
    private String to;          // Email recipient

    @NonNull
    private String subject;     // Email subject

    private String body;        // Text body of the email

    @NonNull
    private Long receivedAt;    // Epoch milliseconds (parsed from internalDate)

}
