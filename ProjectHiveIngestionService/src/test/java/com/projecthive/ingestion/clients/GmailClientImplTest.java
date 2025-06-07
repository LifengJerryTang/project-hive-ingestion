package com.projecthive.ingestion.clients;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.projecthive.ingestion.auth.GmailAuthProvider;
import com.projecthive.ingestion.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GmailClientImplTest {

    @Mock
    private Gmail mockGmail;

    @Mock
    private Gmail.Users mockUsers;

    @Mock
    private Gmail.Users.Messages mockMessages;

    @Mock
    private Gmail.Users.Messages.List mockList;

    @Mock
    private Gmail.Users.Messages.Get mockGet;

    @Mock
    private GmailAuthProvider mockAuthProvider;

    @Mock
    private Message mockMessageSummary;

    @Mock
    private Message mockFullMessage;

    private GmailClientImpl gmailClient;

    @BeforeEach
    public void setUp() throws Exception {
        when(mockAuthProvider.createGmailClient()).thenReturn(mockGmail);
        when(mockGmail.users()).thenReturn(mockUsers);
        when(mockUsers.messages()).thenReturn(mockMessages);
        gmailClient = new GmailClientImpl(mockAuthProvider);
    }

    @Test
    public void fetchUnreadMessages_returnsFullMessagesSuccessfully() throws IOException {
        final ListMessagesResponse response = new ListMessagesResponse();
        response.setMessages(Collections.singletonList(mockMessageSummary));

        when(mockMessages.list(TestConstants.USER_ID)).thenReturn(mockList);
        when(mockList.setQ(TestConstants.QUERY_UNREAD)).thenReturn(mockList);
        when(mockList.execute()).thenReturn(response);
        when(mockMessageSummary.getId()).thenReturn(TestConstants.MSG_ID_1);
        when(mockMessages.get(TestConstants.USER_ID, TestConstants.MSG_ID_1)).thenReturn(mockGet);
        when(mockGet.execute()).thenReturn(mockFullMessage);

        final List<Message> result = gmailClient.fetchUnreadMessages();

        assertEquals(1, result.size());
        assertEquals(mockFullMessage, result.getFirst());
    }

    @Test
    public void fetchUnreadMessages_handlesNullMessagesListGracefully() throws IOException {
        final ListMessagesResponse response = new ListMessagesResponse();
        response.setMessages(null);

        when(mockMessages.list(TestConstants.USER_ID)).thenReturn(mockList);
        when(mockList.setQ(TestConstants.QUERY_UNREAD)).thenReturn(mockList);
        when(mockList.execute()).thenReturn(response);

        final List<Message> result = gmailClient.fetchUnreadMessages();

        assertEquals(0, result.size());
    }

    @Test
    public void fetchUnreadMessages_throwsRuntimeExceptionOnIOException() throws IOException {
        when(mockMessages.list(TestConstants.USER_ID)).thenReturn(mockList);
        when(mockList.setQ(TestConstants.QUERY_UNREAD)).thenReturn(mockList);
        when(mockList.execute()).thenThrow(new IOException(TestConstants.ERROR_MESSAGE));

        assertThrows(RuntimeException.class, () -> gmailClient.fetchUnreadMessages());
    }
}
