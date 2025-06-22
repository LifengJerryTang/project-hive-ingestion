package com.projecthive.ingestion.parser;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.projecthive.ingestion.constants.TestConstants;
import com.projecthive.ingestion.models.GmailMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GmailMessageParserTest {

    @Test
    public void testParse_returnsParsedGmailMessage() {
        // Arrange
        final MessagePartHeader fromHeader = new MessagePartHeader()
                .setName("From")
                .setValue(TestConstants.SENDER_1);

        final MessagePartHeader toHeader = new MessagePartHeader()
                .setName("To")
                .setValue(TestConstants.RECEIVER_1);

        final MessagePartHeader subjectHeader = new MessagePartHeader()
                .setName("Subject")
                .setValue(TestConstants.SUBJECT_1);

        final MessagePart payload = new MessagePart()
                .setHeaders(Arrays.asList(fromHeader, toHeader, subjectHeader));

        final Message gmailMessage = new Message()
                .setId(TestConstants.MSG_ID_1)
                .setSnippet(TestConstants.SNIPPET_1)
                .setInternalDate(TestConstants.RECEIVED_AT_1)
                .setPayload(payload);

        final GmailMessageParser parser = new GmailMessageParser();

        // Act
        final GmailMessage result = parser.parse(gmailMessage);

        // Assert
        assertEquals(TestConstants.MSG_ID_1, result.getId());
        assertEquals(TestConstants.SENDER_1, result.getFrom());
        assertEquals(TestConstants.RECEIVER_1, result.getTo());
        assertEquals(TestConstants.SUBJECT_1, result.getSubject());
        assertEquals(TestConstants.SNIPPET_1, result.getBody());
        assertEquals(TestConstants.RECEIVED_AT_1, result.getReceivedAt());
    }

    @Test
    public void testParse_missingHeaders_returnsEmptyStrings() {
        // Arrange
        final MessagePart payload = new MessagePart().setHeaders(Collections.emptyList());

        final Message gmailMessage = new Message()
                .setId(TestConstants.MSG_ID_2)
                .setSnippet(TestConstants.SNIPPET_2)
                .setInternalDate(0L)
                .setPayload(payload);

        final GmailMessageParser parser = new GmailMessageParser();

        // Act
        final GmailMessage result = parser.parse(gmailMessage);

        // Assert
        assertEquals("", result.getFrom());
        assertEquals("", result.getTo());
        assertEquals("", result.getSubject());
    }
    
    @Test
    public void testParse_nullPayload_handlesGracefully() {
        // Arrange
        final Message gmailMessage = new Message()
                .setId(TestConstants.MSG_ID_1)
                .setSnippet(TestConstants.SNIPPET_1)
                .setInternalDate(TestConstants.RECEIVED_AT_1)
                .setPayload(null);

        final GmailMessageParser parser = new GmailMessageParser();

        // Act
        final GmailMessage result = parser.parse(gmailMessage);
        
        // Assert
        assertEquals(TestConstants.MSG_ID_1, result.getId());
        assertEquals(TestConstants.SNIPPET_1, result.getBody());
        assertEquals("", result.getFrom());
        assertEquals("", result.getTo());
        assertEquals("", result.getSubject());
    }
    
    @Test
    public void testParse_caseInsensitiveHeaderMatching() {
        // Arrange
        final MessagePartHeader fromHeader = new MessagePartHeader()
                .setName("FROM") // Uppercase
                .setValue(TestConstants.SENDER_1);

        final MessagePartHeader toHeader = new MessagePartHeader()
                .setName("to") // Lowercase
                .setValue(TestConstants.RECEIVER_1);

        final MessagePartHeader subjectHeader = new MessagePartHeader()
                .setName("Subject") // Mixed case
                .setValue(TestConstants.SUBJECT_1);

        final MessagePart payload = new MessagePart()
                .setHeaders(Arrays.asList(fromHeader, toHeader, subjectHeader));

        final Message gmailMessage = new Message()
                .setId(TestConstants.MSG_ID_1)
                .setSnippet(TestConstants.SNIPPET_1)
                .setInternalDate(TestConstants.RECEIVED_AT_1)
                .setPayload(payload);

        final GmailMessageParser parser = new GmailMessageParser();

        // Act
        final GmailMessage result = parser.parse(gmailMessage);

        // Assert
        assertEquals(TestConstants.SENDER_1, result.getFrom());
        assertEquals(TestConstants.RECEIVER_1, result.getTo());
        assertEquals(TestConstants.SUBJECT_1, result.getSubject());
    }
    
    @Test
    public void testParse_withNullMessage_handlesGracefully() {
        // Arrange
        final GmailMessageParser parser = new GmailMessageParser();
        
        // Act & Assert
        try {
            parser.parse(null);
            // If we get here, the test fails because we expect an exception
            throw new AssertionError("Parser should throw exception for null message");
        } catch (NullPointerException e) {
            // Expected behavior - test passes
            assertNotNull(e);
        }
    }
}
