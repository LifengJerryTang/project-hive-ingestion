package com.projecthive.ingestion.utilities;


import com.projecthive.ingestion.models.GmailMessage;
import com.projecthive.ingestion.models.Message;

import java.util.UUID;

import static com.projecthive.ingestion.constants.CommonConstants.GMAIL;

public final class MessageConverter {

    public static Message fromGmail(GmailMessage gmailMessage) {
        return Message.builder()
                .id(UUID.randomUUID().toString()) // Internal ID
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