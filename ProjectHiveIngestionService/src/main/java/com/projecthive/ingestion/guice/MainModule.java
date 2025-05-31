package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GmailModule());
        install(new DynamoDbModule());
    }
}
