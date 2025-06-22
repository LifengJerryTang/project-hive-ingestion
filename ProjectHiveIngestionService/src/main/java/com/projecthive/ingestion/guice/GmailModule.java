package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import com.projecthive.ingestion.auth.GmailCredentialConfig;
import com.projecthive.ingestion.clients.GmailClient;
import com.projecthive.ingestion.clients.GmailClientImpl;
import com.projecthive.ingestion.controllers.GmailIngestionController;
import com.projecthive.ingestion.parser.GmailMessageParser;

import com.google.inject.Singleton;

import static com.projecthive.ingestion.constants.CommonConstants.*;

public class GmailModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GmailAuthProvider.class).in(Singleton.class);
        bind(GmailClient.class).to(GmailClientImpl.class).in(Singleton.class);
        bind(GmailMessageParser.class).in(Singleton.class);
        bind(GmailIngestionController.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public GmailCredentialConfig provideGmailCredentialConfig() {
        final String clientId = System.getenv(GMAIL_CLIENT_ID);
        final String clientSecret = System.getenv(GMAIL_CLIENT_SECRET);
        final String refreshToken = System.getenv(GMAIL_REFRESH_TOKEN);

        if (clientId == null || clientSecret == null || refreshToken == null) {
            throw new IllegalStateException("Missing required Gmail OAuth environment variables");
        }

        return new GmailCredentialConfig(clientId, clientSecret, refreshToken);
    }
}
