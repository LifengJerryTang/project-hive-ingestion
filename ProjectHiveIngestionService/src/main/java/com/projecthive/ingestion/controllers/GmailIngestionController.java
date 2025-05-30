package com.projecthive.ingestion.controllers;

import com.projecthive.ingestion.clients.GmailClient;
import com.projecthive.ingestion.dao.MessageDao;
import com.projecthive.ingestion.models.GmailMessage;
import com.projecthive.ingestion.models.Message;
import com.projecthive.ingestion.parser.GmailMessageParser;
import com.projecthive.ingestion.utilities.MessageConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GmailIngestionController {
    private static final Logger logger = LogManager.getLogger(GmailIngestionController.class);

    private final GmailClient gmailClient;
    private final GmailMessageParser messageParser;
    private final MessageDao messageDao;

    @Inject
    public GmailIngestionController(
            GmailClient gmailClient,
            GmailMessageParser messageParser,
            MessageDao messageDao
    ) {
        this.gmailClient = gmailClient;
        this.messageParser = messageParser;
        this.messageDao = messageDao;
    }

    public void ingestGmailMessages() throws GeneralSecurityException, IOException {
        final List<com.google.api.services.gmail.model.Message> gmailMessages = gmailClient.fetchUnreadMessages();

        for (final com.google.api.services.gmail.model.Message gmailMessage : gmailMessages) {
            final GmailMessage parsedGmailMessage = messageParser.parse(gmailMessage);
            final Message messageToBeSaved = MessageConverter.fromGmail(parsedGmailMessage);
            messageDao.save(messageToBeSaved);
        }
    }
}
