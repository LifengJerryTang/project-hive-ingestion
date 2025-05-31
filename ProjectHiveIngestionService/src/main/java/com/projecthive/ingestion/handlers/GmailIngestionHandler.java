package com.projecthive.ingestion.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.projecthive.ingestion.controllers.GmailIngestionController;
import com.projecthive.ingestion.guice.MainModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import static com.projecthive.ingestion.constants.CommonConstants.*;

public class GmailIngestionHandler implements RequestHandler<ScheduledEvent, Void>{

    private static final Logger logger = LogManager.getLogger(GmailIngestionHandler.class);

    private final GmailIngestionController gmailIngestionController;

    public GmailIngestionHandler() {
        final Injector injector = Guice.createInjector(new MainModule());
        this.gmailIngestionController = injector.getInstance(GmailIngestionController.class);
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        try {
            // Set MDC metadata
            ThreadContext.put(MDC_SOURCE_FIELD, GMAIL);
            ThreadContext.put(MDC_REQUEST_ID_FIELD, UUID.randomUUID().toString());
            ThreadContext.put(MDC_EVENT_ID_FIELD, event.getId());

            logger.info("EventBridge trigger received: {}", event.getDetailType());

            gmailIngestionController.ingestGmailMessages();

            logger.info("Ingestion completed successfully");
        } catch (final GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            ThreadContext.clearAll();
        }
        return null;
    }
}
