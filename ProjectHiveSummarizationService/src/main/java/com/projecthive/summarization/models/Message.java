package com.projecthive.summarization.models;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @NonNull
    private String id; // Internal UUID

    @NonNull
    private String username;

    @NonNull
    private String platform; // "gmail", "discord", etc.

    @NonNull
    private String platformMessageId; // Gmail message ID, Discord message ID, etc.

    @NonNull
    private String recipient; // "to" for email, channel ID for Discord/Slack

    @NonNull
    private String sender;

    @NonNull
    private Long receivedAt; // Epoch millis

    private String subject;

    private String body;

    private Map<String, String> metadata;

}

