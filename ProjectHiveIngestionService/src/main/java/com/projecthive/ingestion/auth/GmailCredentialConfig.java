package com.projecthive.ingestion.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmailCredentialConfig {
    @NonNull
    private String clientId;

    @NonNull
    private String clientSecret;

    @NonNull
    private String refreshToken;
}
