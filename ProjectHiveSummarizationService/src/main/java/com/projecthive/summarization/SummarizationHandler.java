package com.projecthive.summarization;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.projecthive.summarization.controller.SummarizationController;
import com.projecthive.summarization.guice.AwsClientModule;
import com.projecthive.summarization.guice.BedrockModule;
import com.projecthive.summarization.guice.DaoModule;
import com.projecthive.summarization.guice.SummarizationModule;
import com.projecthive.summarization.models.Message;
import com.projecthive.summarization.constants.DynamoDbConstants;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

import javax.annotation.Nonnull;
import java.util.List;

import static com.projecthive.summarization.utilities.StreamRecordConverter.convertToMessage;

/**
 * AWS Lambda handler for processing DynamoDB Stream events to trigger message summarization.
 * 
 * This handler is invoked when new messages are inserted into the DynamoDB messages table.
 * It uses the Enhanced Client approach for efficient conversion of DynamoDB Stream records
 * to Java objects, eliminating the need for complex manual AttributeValue mapping.
 * 
 * Architecture Benefits:
 * - MAINTAINABILITY: Reduced from 50+ lines of manual conversion to 1 line of declarative code
 * - TYPE SAFETY: Compile-time validation vs runtime AttributeValue casting errors
 * - AWS BEST PRACTICE: Uses official AWS SDK conversion mechanisms
 * - PERFORMANCE: Optimized conversion logic maintained by AWS team
 * - FUTURE-PROOF: Automatic support for new DynamoDB features and data types
 */
public class SummarizationHandler implements RequestHandler<DynamodbEvent, Void> {

    private final SummarizationController controller;

    public SummarizationHandler() {
        Injector injector = Guice.createInjector(
                new AwsClientModule(),
                new DaoModule(),
                new BedrockModule(),
                new SummarizationModule()
        );
        this.controller = injector.getInstance(SummarizationController.class);
    }

    @Override
    public Void handleRequest(
            @Nonnull final DynamodbEvent event,
            @Nonnull final Context context
    ) {
        List<DynamodbEvent.DynamodbStreamRecord> records = event.getRecords();

        for (DynamodbEvent.DynamodbStreamRecord record : records) {
            if (DynamoDbConstants.EVENT_NAME_INSERT.equals(record.getEventName())) {
                // Use Enhanced Client approach to convert DynamoDB Stream record to Message object
                // This replaces the complex manual AttributeValue conversion with AWS SDK's built-in mapping
                Message message = convertToMessage(record);
                controller.summarizeMessage(message);
            }
        }

        return null;
    }


}
