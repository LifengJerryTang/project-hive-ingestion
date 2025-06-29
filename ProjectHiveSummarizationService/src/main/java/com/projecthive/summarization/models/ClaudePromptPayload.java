package com.projecthive.summarization.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.projecthive.summarization.constants.PromptConstants.JSON_ANTHROPIC_VERSION;
import static com.projecthive.summarization.constants.PromptConstants.JSON_MAX_TOKENS;
import static com.projecthive.summarization.constants.PromptConstants.JSON_MESSAGES;
import static com.projecthive.summarization.constants.PromptConstants.JSON_TEMPERATURE;
import static com.projecthive.summarization.constants.PromptConstants.JSON_TOP_K;
import static com.projecthive.summarization.constants.PromptConstants.JSON_TOP_P;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaudePromptPayload implements BedrockPromptPayload {

    @JsonProperty(JSON_ANTHROPIC_VERSION)
    private String anthropicVersion;

    @JsonProperty(JSON_MAX_TOKENS)
    private int maxTokens;

    @JsonProperty(JSON_TEMPERATURE)
    private double temperature;

    @JsonProperty(JSON_TOP_K)
    private int topK;

    @JsonProperty(JSON_TOP_P)
    private double topP;

    @JsonProperty(JSON_MESSAGES)
    private List<PromptMessage> promptMessages;
}