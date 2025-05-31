package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import com.projecthive.ingestion.auth.GmailCredentialConfig;
import com.projecthive.ingestion.clients.GmailClient;
import com.projecthive.ingestion.clients.GmailClientImpl;
import com.projecthive.ingestion.controllers.GmailIngestionController;
import com.projecthive.ingestion.parser.GmailMessageParser;

import static com.google.inject.Scopes.SINGLETON;

public class GmailModule extends AbstractModule {

    @Override
    protected void configure() {
        // Gmail auth
        bind(GmailAuthProvider.class).in(SINGLETON);
        bind(GmailCredentialConfig.class).toInstance(loadConfigFromEnv());

        // Gmail API client
        bind(GmailClient.class).to(GmailClientImpl.class).in(SINGLETON);

        // Message parsing and ingestion
        bind(GmailMessageParser.class).in(SINGLETON);
        bind(GmailIngestionController.class).in(SINGLETON);
    }

    private GmailCredentialConfig loadConfigFromEnv() {
        final String clientId = System.getenv("GMAIL_CLIENT_ID");
        final String clientSecret = System.getenv("GMAIL_CLIENT_SECRET");
        final String refreshToken = System.getenv("GMAIL_REFRESH_TOKEN");

        if (clientId == null || clientSecret == null || refreshToken == null) {
            throw new IllegalStateException("Missing required Gmail OAuth environment variables");
        }

        return new GmailCredentialConfig(clientId, clientSecret, refreshToken);
    }
}
