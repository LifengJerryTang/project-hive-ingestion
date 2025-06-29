package com.projecthive.summarization.models;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import static com.projecthive.summarization.constants.DynamoDbConstants.GSI_TIMESTAMP;
import static com.projecthive.summarization.constants.DynamoDbConstants.GSI_USERNAME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@Builder
public class Summary {

    @NonNull
    private String summaryId;

    @NonNull
    private String username;

    @NonNull
    private String timestamp;

    @NonNull
    private String summaryText;

    @NonNull
    private String source;

    @NonNull
    private String messageId; // message foreign key

    @NonNull
    private String messageSender;

    @NonNull
    private Long messageReceivedAt;

    private String messageSubject;


    @DynamoDbPartitionKey
    public String getSummaryId() {
        return summaryId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI_USERNAME})
    public String getUsername() {
        return username;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI_TIMESTAMP})
    public String getTimestamp() {
        return timestamp;
    }
}
