package com.projecthive.ingestion.guice;

import com.google.inject.AbstractModule;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import com.projecthive.ingestion.auth.GmailCredentialConfig;
import com.projecthive.ingestion.clients.GmailClient;
import com.projecthive.ingestion.clients.GmailClientImpl;

public class GmailModule extends AbstractModule {

    @Override
    protected void configure() {
        // Gmail components
        bind(GmailClient.class).to(GmailClientImpl.class);
        bind(GmailAuthProvider.class);

        // Inject static config for GmailAuthProvider
        bind(GmailCredentialConfig.class).toInstance(loadConfigFromEnv());
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
