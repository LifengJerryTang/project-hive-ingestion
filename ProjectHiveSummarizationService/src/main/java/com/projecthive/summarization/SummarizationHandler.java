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

import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import software.amazon.awssdk.core.SdkBytes;

import static com.projecthive.summarization.utilities.DynamoDbUtils.convertAttributeMap;

public class SummarizationHandler implements RequestHandler<DynamodbEvent, Void> {

    private static final TableSchema<Message> MESSAGE_SCHEMA =
            TableSchema.fromBean(Message.class);

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
                Map<String, AttributeValue> lambdaAttributeMap = record.getDynamodb().getNewImage();
                Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> sdkAttributeMap =
                    convertAttributeMap(lambdaAttributeMap);
                Message message = MESSAGE_SCHEMA.mapToItem(sdkAttributeMap);
                controller.summarizeMessage(message);
            }
        }

        return null;
    }


}
