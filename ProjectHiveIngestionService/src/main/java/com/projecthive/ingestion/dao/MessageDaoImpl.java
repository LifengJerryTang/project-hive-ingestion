package com.projecthive.ingestion.dao;

import com.projecthive.ingestion.exceptions.DaoDataAccessException;
import com.projecthive.ingestion.models.Message;
import lombok.Generated;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;

public class MessageDaoImpl implements MessageDao {

    private final DynamoDbTable<Message> messagesTable;
    private static final String MESSAGES_TABLE = "messages";

    @Inject
    @Generated
    public MessageDaoImpl(@NonNull final DynamoDbClient dynamoDbClient) {
        final DynamoDbEnhancedClient enhancedClient = buildEnhancedClient(dynamoDbClient);

        this.messagesTable = enhancedClient.table(MESSAGES_TABLE, TableSchema.fromBean(Message.class));
    }

    @Generated
    protected DynamoDbEnhancedClient buildEnhancedClient(@NonNull final DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }

    @Override
    public void save(@NonNull final Message message) {
        try {
            messagesTable.putItem(PutItemEnhancedRequest.builder(Message.class)
                    .item(message)
                    .build());
        } catch (final Exception e) {
            throw new DaoDataAccessException("Failed to save message to DynamoDB", e);
        }
    }
}
