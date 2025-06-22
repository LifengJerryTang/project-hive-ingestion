package com.projecthive.ingestion.utilities;


import com.projecthive.ingestion.models.GmailMessage;
import com.projecthive.ingestion.models.Message;
import lombok.NonNull;

import java.util.UUID;

import static com.projecthive.ingestion.constants.CommonConstants.GMAIL;

public final class MessageConverter {

    public static Message fromGmail(@NonNull final GmailMessage gmailMessage) {
        return Message.builder()
                .id(UUID.randomUUID().toString()) // Internal ID
                .username("jerrytang") // Using a test username for now
                .platform(GMAIL)
                .platformMessageId(gmailMessage.getId())
                .recipient(gmailMessage.getTo())
                .sender(gmailMessage.getFrom())
                .subject(gmailMessage.getSubject())
                .body(gmailMessage.getBody())
                .receivedAt(gmailMessage.getReceivedAt())
                .build();
    }
}