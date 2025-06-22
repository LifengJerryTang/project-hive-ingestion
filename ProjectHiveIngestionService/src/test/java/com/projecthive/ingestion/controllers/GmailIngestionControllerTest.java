package com.projecthive.ingestion.controllers;

import com.google.api.services.gmail.model.Message;
import com.projecthive.ingestion.clients.GmailClient;
import com.projecthive.ingestion.constants.TestConstants;
import com.projecthive.ingestion.dao.MessageDao;
import com.projecthive.ingestion.models.GmailMessage;
import com.projecthive.ingestion.parser.GmailMessageParser;
import com.projecthive.ingestion.utilities.MessageConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GmailIngestionControllerTest {

    @Mock
    private GmailClient gmailClient;

    @Mock
    private GmailMessageParser messageParser;

    @Mock
    private MessageDao messageDao;

    @InjectMocks
    private GmailIngestionController controller;

    @Test
    public void testIngestGmailMessages_savesParsedMessages() throws Exception {
        // Arrange
        final Message raw1 = new Message().setId(TestConstants.MSG_ID_1);
        final Message raw2 = new Message().setId(TestConstants.MSG_ID_2);
        final List<Message> rawMessages = Arrays.asList(raw1, raw2);

        final GmailMessage parsed1 = GmailMessage.builder()
                .id(TestConstants.MSG_ID_1)
                .from(TestConstants.SENDER_1)
                .to(TestConstants.RECEIVER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(1000L)
                .build();

        final GmailMessage parsed2 = GmailMessage.builder()
                .id(TestConstants.MSG_ID_2)
                .from(TestConstants.SENDER_2)
                .to(TestConstants.RECEIVER_2)
                .subject(TestConstants.SUBJECT_2)
                .body(TestConstants.SNIPPET_2)
                .receivedAt(2000L)
                .build();

        when(gmailClient.fetchUnreadMessages()).thenReturn(rawMessages);
        when(messageParser.parse(raw1)).thenReturn(parsed1);
        when(messageParser.parse(raw2)).thenReturn(parsed2);

        // Act
        controller.ingestGmailMessages();

        // Assert
        final ArgumentCaptor<com.projecthive.ingestion.models.Message> captor =
                ArgumentCaptor.forClass(com.projecthive.ingestion.models.Message.class);
        verify(messageDao, times(2)).save(captor.capture());
    }
    
    @Test
    public void testIngestGmailMessages_noUnreadMessages_doesNotSaveAnything() throws Exception {
        // Arrange
        when(gmailClient.fetchUnreadMessages()).thenReturn(Collections.emptyList());

        // Act
        controller.ingestGmailMessages();

        // Assert
        verify(messageDao, never()).save(any());
    }
    
    
    @Test
    public void testIngestGmailMessages_verifyMessageConversion() throws GeneralSecurityException, IOException {
        // Arrange
        final Message rawMessage = new Message().setId(TestConstants.MSG_ID_1);
        final List<Message> rawMessages = Collections.singletonList(rawMessage);

        final GmailMessage parsedMessage = GmailMessage.builder()
                .id(TestConstants.MSG_ID_1)
                .from(TestConstants.SENDER_1)
                .to(TestConstants.RECEIVER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        when(gmailClient.fetchUnreadMessages()).thenReturn(rawMessages);
        when(messageParser.parse(rawMessage)).thenReturn(parsedMessage);

        // Act
        controller.ingestGmailMessages();

        // Assert
        final ArgumentCaptor<com.projecthive.ingestion.models.Message> captor =
                ArgumentCaptor.forClass(com.projecthive.ingestion.models.Message.class);
        verify(messageDao).save(captor.capture());
        
        com.projecthive.ingestion.models.Message savedMessage = captor.getValue();
        assertEquals(TestConstants.MSG_ID_1, savedMessage.getPlatformMessageId());
        assertEquals(TestConstants.SENDER_1, savedMessage.getSender());
        assertEquals(TestConstants.RECEIVER_1, savedMessage.getRecipient());
        assertEquals(TestConstants.SUBJECT_1, savedMessage.getSubject());
        assertEquals(TestConstants.SNIPPET_1, savedMessage.getBody());
        assertEquals(TestConstants.RECEIVED_AT_1, savedMessage.getReceivedAt());
    }
}
