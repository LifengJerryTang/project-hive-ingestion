package com.projecthive.ingestion.dao;

import com.projecthive.ingestion.models.Message;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;

public class MessageDaoImpl implements MessageDao {

    private final DynamoDbTable<Message> gmailTable;
    private static final String MESSAGES_TABLE = "messages";

    @Inject
    public MessageDaoImpl(@NonNull final DynamoDbClient dynamoDbClient) {
        final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.gmailTable = enhancedClient.table(MESSAGES_TABLE, TableSchema.fromBean(Message.class));
    }

    @Override
    public void save(@NonNull final Message message) {
        gmailTable.putItem(PutItemEnhancedRequest.builder(Message.class)
                .item(message)
                .build());
    }
}
