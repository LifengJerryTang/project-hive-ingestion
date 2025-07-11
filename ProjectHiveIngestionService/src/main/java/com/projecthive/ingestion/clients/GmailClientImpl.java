package com.projecthive.ingestion.clients;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GmailClientImpl implements GmailClient {
    private static final Logger logger = LogManager.getLogger(GmailClientImpl.class);

    private Gmail gmailService;
    private final String userId = "me"; // 'me' refers to the authenticated user

    @Inject
    public GmailClientImpl(@NonNull final GmailAuthProvider gmailAuthProvider) {
          try {
            this.gmailService = gmailAuthProvider.createGmailClient();
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize Gmail client", e);
        }
    }

    @Override
    public List<Message> fetchUnreadMessages() {
        try {
            final ListMessagesResponse response = gmailService.users()
                    .messages()
                    .list(userId)
                    .setQ("is:unread")
                    .execute();

            final List<Message> messages = new ArrayList<>();
            if (response.getMessages() != null) {
                for (final Message msg : response.getMessages()) {
                    final Message fullMessage = gmailService.users()
                            .messages()
                            .get(userId, msg.getId())
                            .execute();
                    messages.add(fullMessage);
                }
            }

            logger.info("Fetched unread messages successfully");

            return messages;
        } catch (IOException e) {
            final String errorMessage = "Failed to fetch unread messages";
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }


    }
}
