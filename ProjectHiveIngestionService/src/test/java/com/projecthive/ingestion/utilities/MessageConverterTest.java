package com.projecthive.ingestion.utilities;

import com.projecthive.ingestion.constants.TestConstants;
import com.projecthive.ingestion.models.GmailMessage;
import com.projecthive.ingestion.models.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.projecthive.ingestion.constants.CommonConstants.GMAIL;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MessageConverterTest {

    @Test
    public void testFromGmail_convertsCorrectly() {
        // Arrange
        final GmailMessage gmailMessage = GmailMessage.builder()
                .id(TestConstants.MSG_ID_1)
                .from(TestConstants.SENDER_1)
                .to(TestConstants.RECEIVER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        // Act
        final Message result = MessageConverter.fromGmail(gmailMessage);

        // Assert
        assertNotNull(result.getId()); // ID is generated
        assertEquals(GMAIL, result.getPlatform());
        assertEquals(TestConstants.MSG_ID_1, result.getPlatformMessageId());
        assertEquals(TestConstants.RECEIVER_1, result.getRecipient());
        assertEquals(TestConstants.SENDER_1, result.getSender());
        assertEquals(TestConstants.SUBJECT_1, result.getSubject());
        assertEquals(TestConstants.SNIPPET_1, result.getBody());
        assertEquals(TestConstants.RECEIVED_AT_1, result.getReceivedAt());
    }
    
    @Test
    public void testFromGmail_withNullValues_convertsCorrectly() {
        // Arrange
        final GmailMessage gmailMessage = GmailMessage.builder()
                .id(TestConstants.MSG_ID_1)
                .from(TestConstants.SENDER_1)
                .to(TestConstants.RECEIVER_1)
                .subject(null)
                .body(null)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        // Act
        final Message result = MessageConverter.fromGmail(gmailMessage);

        // Assert
        assertNotNull(result.getId()); // ID is generated
        assertEquals(GMAIL, result.getPlatform());
        assertEquals(TestConstants.MSG_ID_1, result.getPlatformMessageId());
        assertEquals(TestConstants.RECEIVER_1, result.getRecipient());
        assertEquals(TestConstants.SENDER_1, result.getSender());
        assertNull(result.getSubject());
        assertNull(result.getBody());
        assertEquals(TestConstants.RECEIVED_AT_1, result.getReceivedAt());
    }
    
    @Test
    public void testFromGmail_generatesUniqueIds() {
        // Arrange
        final GmailMessage gmailMessage = GmailMessage.builder()
                .id(TestConstants.MSG_ID_1)
                .from(TestConstants.SENDER_1)
                .to(TestConstants.RECEIVER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        // Act
        final Message result1 = MessageConverter.fromGmail(gmailMessage);
        final Message result2 = MessageConverter.fromGmail(gmailMessage);

        // Assert
        assertNotEquals(result1.getId(), result2.getId(), "Generated IDs should be unique");
    }
}
