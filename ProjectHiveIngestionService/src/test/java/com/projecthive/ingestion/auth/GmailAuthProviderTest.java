package com.projecthive.ingestion.auth;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.projecthive.ingestion.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GmailAuthProviderTest {

    @Mock
    private GmailCredentialConfig mockConfig;

    private GmailAuthProvider provider;

    @BeforeEach
    public void setUp() {
        when(mockConfig.getClientId()).thenReturn(TestConstants.ID);
        when(mockConfig.getClientSecret()).thenReturn(TestConstants.SECRET);
        when(mockConfig.getRefreshToken()).thenReturn(TestConstants.REFRESH);

        provider = new GmailAuthProvider(mockConfig);
    }

    @Test
    public void createGmailClient_success_returnsGmail() throws Exception {
        try (MockedStatic<GoogleNetHttpTransport> staticMock =
                     org.mockito.Mockito.mockStatic(GoogleNetHttpTransport.class)) {

            final com.google.api.client.http.javanet.NetHttpTransport transport =
                    new com.google.api.client.http.javanet.NetHttpTransport();
            staticMock.when(GoogleNetHttpTransport::newTrustedTransport).thenReturn(transport);

            final Gmail gmail = provider.createGmailClient();
            assertNotNull(gmail);
        }
    }

    @Test
    public void createGmailClient_throwsException_whenTransportFails() {
        try (MockedStatic<GoogleNetHttpTransport> staticMock =
                     org.mockito.Mockito.mockStatic(GoogleNetHttpTransport.class)) {

            staticMock.when(GoogleNetHttpTransport::newTrustedTransport)
                    .thenThrow(new GeneralSecurityException("Boom"));

            assertThrows(GeneralSecurityException.class, () -> provider.createGmailClient());
        }
    }
}