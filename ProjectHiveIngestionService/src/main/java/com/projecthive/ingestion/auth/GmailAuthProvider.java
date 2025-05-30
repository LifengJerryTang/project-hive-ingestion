package com.projecthive.ingestion.auth;

import com.google.api.services.gmail.Gmail;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.UserCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

import javax.inject.Inject;

import static com.projecthive.ingestion.constants.CommonConstants.APPLICATION_NAME;

public class GmailAuthProvider {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GmailCredentialConfig config;

    @Inject
    public GmailAuthProvider(final GmailCredentialConfig config) {
        this.config = config;
    }

    public Gmail createGmailClient() throws Exception {
        final UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret())
                .setRefreshToken(config.getRefreshToken())
                .build();

        final HttpCredentialsAdapter requestInitializer = new HttpCredentialsAdapter(userCredentials);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
