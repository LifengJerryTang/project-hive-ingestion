package com.projecthive.summarization.bedrock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.projecthive.summarization.models.BedrockPromptPayload;
import lombok.NonNull;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;

import static com.projecthive.summarization.constants.HTTPConstants.ACCEPT_JSON;
import static com.projecthive.summarization.constants.HTTPConstants.CONTENT_TYPE_JSON;

public class BedrockModelInvoker {

    private final BedrockRuntimeClient client;
    private final ObjectMapper objectMapper;

    @Inject
    public BedrockModelInvoker() {
        this.client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Invoke any Bedrock model with a typed prompt payload.
     *
     * @param modelId Bedrock model identifier (e.g. SupportedModel.CLAUDE_SONNET.getModelId())
     * @param payload typed prompt payload implementing BedrockPromptPayload
     * @return raw JSON response
     */
    public String invokeModel(@NonNull final String modelId, @NonNull final BedrockPromptPayload payload) {
        try {
            final String json = objectMapper.writeValueAsString(payload);

            final InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType(CONTENT_TYPE_JSON)
                    .accept(ACCEPT_JSON)
                    .body(SdkBytes.fromString(json, StandardCharsets.UTF_8))
                    .build();

            final InvokeModelResponse response = client.invokeModel(request);

            return response.body().asUtf8String();
        } catch (final Exception e) {
            throw new RuntimeException("Failed to invoke Bedrock model", e);
        }
    }
}