package com.projecthive.summarization.models;

import lombok.NonNull;

public enum SupportedModel {
    CLAUDE_SONNET("anthropic.claude-3-sonnet-20240229-v1:0");

    private final String modelId;

    SupportedModel(@NonNull final String modelId) {
        this.modelId = modelId;
    }

    /**
     * @return the Bedrock model identifier to pass to InvokeModel
     */
    public String getModelId() {
        return modelId;
    }

    @Override
    public String toString() {
        return modelId;
    }
}