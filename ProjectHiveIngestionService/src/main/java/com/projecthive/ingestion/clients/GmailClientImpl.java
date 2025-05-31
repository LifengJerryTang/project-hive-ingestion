package com.projecthive.ingestion.clients;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GmailClientImpl implements GmailClient {
    private static final Logger logger = LogManager.getLogger(GmailClientImpl.class);

    private final Gmail gmailService;
    private final String userId = "me"; // 'me' refers to the authenticated user

    @Inject
    public GmailClientImpl(@NonNull final GmailAuthProvider gmailAuthProvider) throws Exception {
        this.gmailService = gmailAuthProvider.createGmailClient();
    }

    @Override
    public List<Message> fetchUnreadMessages() {
        try {
            ListMessagesResponse response = gmailService.users()
                    .messages()
                    .list(userId)
                    .setQ("is:unread")
                    .execute();

            List<Message> messages = new ArrayList<>();
            if (response.getMessages() != null) {
                for (Message msg : response.getMessages()) {
                    Message fullMessage = gmailService.users()
                            .messages()
                            .get("me", msg.getId())
                            .execute();
                    messages.add(fullMessage);
                }
            }

            return messages;
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch unread messages", e);
        }
    }
}
