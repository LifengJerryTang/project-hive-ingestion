package com.projecthive.ingestion.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.projecthive.ingestion.constants.TestConstants;
import com.projecthive.ingestion.controllers.GmailIngestionController;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailIngestionHandlerTest {

    @Mock
    private GmailIngestionController mockController;

    @Mock
    private Context mockContext;

    private GmailIngestionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GmailIngestionHandler(mockController);
    }

    @Test
    void handleRequest_shouldIngestGmailMessagesSuccessfully() throws GeneralSecurityException, IOException {
        final ScheduledEvent event = new ScheduledEvent();
        event.setId(TestConstants.EVENT_ID);
        event.setDetailType(TestConstants.DETAIL_TYPE);

        handler.handleRequest(event, mockContext);

        verify(mockController).ingestGmailMessages();
        verifyNoMoreInteractions(mockController);

        // Optional: Assert MDC is cleared
        assert ThreadContext.getContext().isEmpty();
    }

    @Test
    void handleRequest_shouldThrowRuntimeException_onException() throws Exception {
        final ScheduledEvent event = new ScheduledEvent();
        event.setId(TestConstants.EVENT_ID);
        event.setDetailType(TestConstants.DETAIL_TYPE);

        doThrow(new RuntimeException("Mocked exception")).when(mockController).ingestGmailMessages();


            assertThrows(RuntimeException.class, () -> handler.handleRequest(event, mockContext));

        verify(mockController).ingestGmailMessages();
        verifyNoMoreInteractions(mockController);

        // Optional: Assert MDC is cleared even after exception
        assert ThreadContext.getContext().isEmpty();
    }

    @Test
    void handleRequest_shouldThrowRuntimeException_onGeneralSecurityException() throws Exception {
        // Arrange
        final ScheduledEvent event = new ScheduledEvent();
        event.setId(TestConstants.EVENT_ID);
        event.setDetailType(TestConstants.DETAIL_TYPE);

        doThrow(new GeneralSecurityException())
                .when(mockController)
                .ingestGmailMessages();

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                handler.handleRequest(event, mockContext)
        );

        verify(mockController).ingestGmailMessages();
        verifyNoMoreInteractions(mockController);
        assert ThreadContext.getContext().isEmpty();
    }

    @Test
    void handleRequest_shouldThrowRuntimeException_onIOException() throws Exception {
        // Arrange
        final ScheduledEvent event = new ScheduledEvent();
        event.setId(TestConstants.EVENT_ID);
        event.setDetailType(TestConstants.DETAIL_TYPE);

        doThrow(new IOException())
                .when(mockController)
                .ingestGmailMessages();

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                handler.handleRequest(event, mockContext)
        );

        verify(mockController).ingestGmailMessages();
        verifyNoMoreInteractions(mockController);
        assert ThreadContext.getContext().isEmpty();
    }

}
