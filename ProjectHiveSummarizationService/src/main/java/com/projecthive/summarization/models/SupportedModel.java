package com.projecthive.summarization.models;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum SupportedModel {
    CLAUDE_SONNET_4_5("global.anthropic.claude-sonnet-4-5-20250929-v1:0");

    private final String modelId;

    SupportedModel(@NonNull final String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return modelId;
    }
}