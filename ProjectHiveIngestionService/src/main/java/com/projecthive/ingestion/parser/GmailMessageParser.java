package com.projecthive.ingestion.parser;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.projecthive.ingestion.models.GmailMessage;
import lombok.NonNull;

import java.util.List;

public class GmailMessageParser {

    public GmailMessage parse(@NonNull final Message message) {
        return GmailMessage.builder()
                .id(message.getId())
                .from(extractHeader(message, "From"))
                .to(extractHeader(message, "To"))
                .subject(extractHeader(message, "Subject"))
                .body(message.getSnippet()) // could extract full body later
                .receivedAt(message.getInternalDate())
                .build();
    }

    private String extractHeader(@NonNull final Message message, @NonNull final String headerName) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        for (MessagePartHeader header : headers) {
            if (headerName.equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }
        return "";
    }
}
