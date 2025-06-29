package com.projecthive.summarization.dao;

import com.google.inject.Inject;
import com.projecthive.summarization.exceptions.DaoDataAccessException;
import com.projecthive.summarization.models.Summary;
import lombok.Generated;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class SummaryDaoImpl implements SummaryDao {

    private final DynamoDbTable<Summary> summaryTable;
    private static final String SUMMARY_TABLE_NAME = "summaries";

    @Inject
    public SummaryDaoImpl(@NonNull final DynamoDbClient dynamoDbClient) {
        final DynamoDbEnhancedClient enhancedClient = buildEnhancedClient(dynamoDbClient);
        this.summaryTable = enhancedClient.table(SUMMARY_TABLE_NAME, TableSchema.fromBean(Summary.class));
    }

    @Generated
    protected DynamoDbEnhancedClient buildEnhancedClient(@NonNull final DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }


    @Override
    public void save(@NonNull final Summary summary) {
        try {
            summaryTable.putItem(PutItemEnhancedRequest.builder(Summary.class)
                    .item(summary)
                    .build());
        } catch (final Exception e) {
            throw new DaoDataAccessException("Failed to save summary to DynamoDB", e);
        }
    }
}
