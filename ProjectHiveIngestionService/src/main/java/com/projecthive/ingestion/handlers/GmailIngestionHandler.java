package com.projecthive.ingestion.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.projecthive.ingestion.controllers.GmailIngestionController;
import com.projecthive.ingestion.guice.GmailModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.UUID;

import static com.projecthive.ingestion.constants.CommonConstants.*;

public class GmailIngestionHandler implements RequestHandler<ScheduledEvent, String> {

    private static final Logger logger = LogManager.getLogger(GmailIngestionHandler.class);

    private final GmailIngestionController gmailIngestionController;

    public GmailIngestionHandler() {
        Injector injector = Guice.createInjector(new GmailModule());
        this.gmailIngestionController = injector.getInstance(GmailIngestionController.class);
    }

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        try {
            // Set MDC metadata
            ThreadContext.put(MDC_SOURCE_FIELD, GMAIL);
            ThreadContext.put(MDC_REQUEST_ID_FIELD, UUID.randomUUID().toString());
            ThreadContext.put(MDC_EVENT_ID_FIELD, event.getId());

            logger.info("EventBridge trigger received: {}", event.getDetailType());

            gmailIngestionController.ingestGmailMessages();

            logger.info("Ingestion completed successfully");
            return "Success";
        } catch (Exception e) {
            logger.error("Unhandled error during ingestion", e);
            throw e;
        } finally {
            ThreadContext.clearAll();
        }
    }
}
