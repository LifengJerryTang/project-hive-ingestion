package com.projecthive.ingestion.clients;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GmailClient {
    List<Message> fetchUnreadMessages() throws IOException, GeneralSecurityException;
}
