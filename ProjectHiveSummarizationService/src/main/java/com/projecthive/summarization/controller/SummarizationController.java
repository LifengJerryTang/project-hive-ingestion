package com.projecthive.summarization.controller;

import com.google.inject.Inject;
import com.projecthive.summarization.bedrock.BedrockModelInvoker;
import com.projecthive.summarization.models.ClaudePromptPayload;
import com.projecthive.summarization.models.Message;
import com.projecthive.summarization.models.PromptMessage;
import com.projecthive.summarization.models.Summary;
import lombok.NonNull;

import com.projecthive.summarization.dao.SummaryDao;


import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static com.projecthive.summarization.constants.PromptConstants.ANTHROPIC_VERSION_VALUE;
import static com.projecthive.summarization.models.SupportedModel.CLAUDE_SONNET;

/**
 * Coordinates the end-to-end summarization flow:
 * 1. Builds a prompt
 * 2. Invokes Bedrock
 * 3. Persists the resulting Summary
 */
public class SummarizationController {

    private final SummaryDao summaryDao;
    private final BedrockModelInvoker modelInvoker;

    @Inject
    public SummarizationController(
            @NonNull final SummaryDao summaryDao,
            @NonNull final BedrockModelInvoker modelInvoker
    ) {
        this.summaryDao    = summaryDao;
        this.modelInvoker  = modelInvoker;
    }

    /**
     * Orchestrates end-to-end summarization of a raw message:
     * <ol>
     *   <li>Generates a unique summaryId and timestamp.</li>
     *   <li>Builds an Anthropic/Claude prompt with carefully chosen sampling parameters:
     *     <ul>
     *       <li>{@code maxTokens=500}: caps summary length (~350–500 words) to prevent truncation or runaway output.</li>
     *       <li>{@code temperature=0.3}: keeps output focused and largely deterministic, while allowing slight variation.</li>
     *       <li>{@code topK=250}: limits token choices to the 250 most likely, reducing hallucination but preserving variety.</li>
     *       <li>{@code topP=0.9}: applies nucleus sampling to the smallest candidate set whose cumulative probability ≥ 0.9.</li>
     *     </ul>
     *   </li>
     *   <li>Invokes the chosen Bedrock model.</li>
     *   <li>Packs the result into a {@link Summary} and persists it via {@link SummaryDao}.</li>
     * </ol>
     *
     * @param message
     *   the incoming raw {@link Message} containing:
     *   <ul>
     *     <li>{@code username} – owner of the message</li>
     *     <li>{@code platform} – e.g. "gmail", "discord"</li>
     *     <li>{@code platformMessageId} – original message ID</li>
     *     <li>{@code body} – the full text to summarize</li>
     *   </ul>
     */
    public void summarizeMessage(@NonNull final Message message) {
        final String summaryId = UUID.randomUUID().toString();
        final String timestamp = Instant.now().toString();

        // Build the prompt
        final PromptMessage promptMessage = PromptMessage.builder()
                .role("user")
                .content("Summarize the following message in 2–3 bullet points:\n\n"
                        + message.getBody())
                .build();

        final ClaudePromptPayload promptPayload = ClaudePromptPayload.builder()
                .anthropicVersion(ANTHROPIC_VERSION_VALUE)
                .maxTokens(500)
                .temperature(0.3)
                .topK(250)
                .topP(0.9)
                .promptMessages(Collections.singletonList(promptMessage))
                .build();

        // Invoke the chosen model
        final String modelOutput = modelInvoker.invokeModel(CLAUDE_SONNET.getModelId(), promptPayload);

        // Persist the summary
        final Summary summary = Summary.builder()
                .summaryId(summaryId)
                .username(message.getUsername())
                .timestamp(timestamp)
                .summaryText(modelOutput)
                .source(message.getPlatform())
                .messageId(message.getId())
                .messageSender(message.getSender())
                .messageReceivedAt(message.getReceivedAt())
                .messageSubject(message.getSubject())
                .build();

        summaryDao.save(summary);
    }
}

