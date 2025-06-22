package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.projecthive.ingestion.dao.MessageDao;
import com.projecthive.ingestion.dao.MessageDaoImpl;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import com.google.inject.Singleton;

public class DynamoDbModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MessageDao.class).to(MessageDaoImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.create();
    }
}
