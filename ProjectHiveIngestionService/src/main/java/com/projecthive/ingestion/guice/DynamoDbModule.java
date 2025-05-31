package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;
import com.projecthive.ingestion.dao.MessageDao;
import com.projecthive.ingestion.dao.MessageDaoImpl;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static com.google.inject.Scopes.SINGLETON;

public class DynamoDbModule extends AbstractModule {
    @Override
    protected void configure() {
        // DAO + DynamoDB
        bind(DynamoDbClient.class).toInstance(DynamoDbClient.create());
        bind(MessageDao.class).to(MessageDaoImpl.class).in(SINGLETON);
    }
}
