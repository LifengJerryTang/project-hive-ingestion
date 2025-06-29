package com.projecthive.summarization.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.projecthive.summarization.constants.PromptConstants.JSON_MESSAGE_ROLE;
import static com.projecthive.summarization.constants.PromptConstants.JSON_MESSAGE_CONTENT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptMessage {

    @JsonProperty(JSON_MESSAGE_ROLE)
    private String role;

    @JsonProperty(JSON_MESSAGE_CONTENT)
    private String content;
}
