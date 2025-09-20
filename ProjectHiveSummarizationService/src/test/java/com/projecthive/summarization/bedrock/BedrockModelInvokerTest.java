package com.projecthive.summarization.bedrock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projecthive.summarization.constants.TestConstants.Models;
import com.projecthive.summarization.constants.TestConstants.Responses;
import com.projecthive.summarization.constants.TestConstants.Errors;
import com.projecthive.summarization.constants.TestConstants.Prompts;
import com.projecthive.summarization.constants.TestConstants.Numbers;
import com.projecthive.summarization.constants.TestConstants.SpecialData;
import com.projecthive.summarization.models.ClaudePromptPayload;
import com.projecthive.summarization.models.PromptMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BedrockModelInvoker to ensure 100% code coverage.
 * Tests AWS Bedrock integration, JSON serialization, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class BedrockModelInvokerTest {

    @Mock
    private BedrockRuntimeClient mockBedrockClient;

    @Mock
    private InvokeModelResponse mockResponse;

    private BedrockModelInvoker invoker;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Create invoker instance
        invoker = new BedrockModelInvoker();
        objectMapper = new ObjectMapper();

        // Use reflection to inject mock client for testing
        Field clientField = BedrockModelInvoker.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(invoker, mockBedrockClient);
    }

    @Test
    void invokeModel_withValidPayload_shouldReturnModelResponse() throws Exception {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload payload = createTestPayload();
        String expectedResponse = Responses.JSON_RESPONSE_WITH_BULLETS;
        
        SdkBytes responseBytes = SdkBytes.fromString(expectedResponse, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(responseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Act
        String result = invoker.invokeModel(modelId, payload);

        // Assert
        assertEquals(expectedResponse, result);
        
        // Verify the request was built correctly
        verify(mockBedrockClient, times(Numbers.TIMES_ONE)).invokeModel(argThat((InvokeModelRequest request) -> {
            assertEquals(modelId, request.modelId());
            assertEquals(Models.CONTENT_TYPE_JSON, request.contentType());
            assertEquals(Models.ACCEPT_JSON, request.accept());
            assertNotNull(request.body());
            return true;
        }));
    }

    @Test
    void invokeModel_withComplexPayload_shouldSerializeCorrectly() throws Exception {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload complexPayload = ClaudePromptPayload.builder()
                .anthropicVersion(Models.ANTHROPIC_VERSION)
                .maxTokens(Models.MAX_TOKENS_HIGH)
                .temperature(Models.TEMPERATURE_HIGH)
                .topK(Models.TOP_K_HIGH)
                .topP(Models.TOP_P_HIGH)
                .promptMessages(Collections.singletonList(
                    PromptMessage.builder()
                        .role(Models.USER_ROLE)
                        .content(SpecialData.COMPLEX_UNICODE_MESSAGE)
                        .build()
                ))
                .build();

        String expectedResponse = Responses.COMPLEX_JSON_RESPONSE;
        SdkBytes responseBytes = SdkBytes.fromString(expectedResponse, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(responseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Act
        String result = invoker.invokeModel(modelId, complexPayload);

        // Assert
        assertEquals(expectedResponse, result);
        
        // Verify the payload was serialized correctly by checking the request body
        verify(mockBedrockClient, times(Numbers.TIMES_ONE)).invokeModel(argThat((InvokeModelRequest request) -> {
            String requestBody = request.body().asUtf8String();
            assertTrue(requestBody.contains(Models.ANTHROPIC_VERSION));
            assertTrue(requestBody.contains(String.valueOf(Models.MAX_TOKENS_HIGH)));
            assertTrue(requestBody.contains(String.valueOf(Models.TEMPERATURE_HIGH)));
            assertTrue(requestBody.contains(String.valueOf(Models.TOP_K_HIGH)));
            assertTrue(requestBody.contains(String.valueOf(Models.TOP_P_HIGH)));
            assertTrue(requestBody.contains("Complex message with special characters"));
            return true;
        }));
    }

    @Test
    void invokeModel_withMinimalPayload_shouldHandleMinimalData() throws Exception {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload minimalPayload = ClaudePromptPayload.builder()
                .anthropicVersion(Models.ANTHROPIC_VERSION)
                .maxTokens(Models.MAX_TOKENS_LOW)
                .temperature(Models.TEMPERATURE_LOW)
                .topK(Models.TOP_K_LOW)
                .topP(Models.TOP_P_LOW)
                .promptMessages(Collections.singletonList(
                    PromptMessage.builder()
                        .role(Models.USER_ROLE)
                        .content(Prompts.SHORT_MESSAGE_CONTENT)
                        .build()
                ))
                .build();

        String expectedResponse = Responses.SHORT_JSON_RESPONSE;
        SdkBytes responseBytes = SdkBytes.fromString(expectedResponse, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(responseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Act
        String result = invoker.invokeModel(modelId, minimalPayload);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void invokeModel_withEmptyResponse_shouldReturnEmptyString() throws Exception {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload payload = createTestPayload();
        
        SdkBytes emptyResponseBytes = SdkBytes.fromString(SpecialData.EMPTY_STRING, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(emptyResponseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Act
        String result = invoker.invokeModel(modelId, payload);

        // Assert
        assertEquals(SpecialData.EMPTY_STRING, result);
    }

    @Test
    void invokeModel_withBedrockException_shouldThrowRuntimeException() {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload payload = createTestPayload();
        
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenThrow(new RuntimeException(Errors.BEDROCK_SERVICE_ERROR));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            invoker.invokeModel(modelId, payload);
        });
    }

    @Test
    void invokeModel_withJsonSerializationError_shouldThrowRuntimeException() throws Exception {
        // Arrange - Create a payload that will cause JSON serialization to fail
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        
        // Use reflection to inject a mock ObjectMapper that throws an exception
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(new RuntimeException(Errors.JSON_SERIALIZATION_ERROR));
        
        Field objectMapperField = BedrockModelInvoker.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(invoker, mockObjectMapper);
        
        ClaudePromptPayload payload = createTestPayload();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            invoker.invokeModel(modelId, payload);
        });
    }

    @Test
    void constructor_shouldInitializeClientAndObjectMapper() throws Exception {
        // Act - Create new invoker to test constructor
        BedrockModelInvoker newInvoker = new BedrockModelInvoker();

        // Assert - Verify fields are initialized
        Field clientField = BedrockModelInvoker.class.getDeclaredField("client");
        clientField.setAccessible(true);
        Object client = clientField.get(newInvoker);
        assertNotNull(client);
        assertTrue(client instanceof BedrockRuntimeClient);

        Field objectMapperField = BedrockModelInvoker.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        Object mapper = objectMapperField.get(newInvoker);
        assertNotNull(mapper);
        assertTrue(mapper instanceof ObjectMapper);
    }

    @Test
    void invokeModel_withDifferentModelIds_shouldAcceptAllModelIds() throws Exception {
        // Test different model IDs
        String[] modelIds = {
            Models.CLAUDE_3_SONNET_MODEL_ID,
            Models.CLAUDE_3_HAIKU_MODEL_ID,
            Models.CLAUDE_INSTANT_MODEL_ID,
            Models.CUSTOM_MODEL_ID
        };

        ClaudePromptPayload payload = createTestPayload();
        String expectedResponse = Responses.GENERIC_JSON_RESPONSE;
        SdkBytes responseBytes = SdkBytes.fromString(expectedResponse, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(responseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Test each model ID
        for (String modelId : modelIds) {
            // Act
            String result = invoker.invokeModel(modelId, payload);

            // Assert
            assertEquals(expectedResponse, result);
        }

        // Verify all model IDs were called
        verify(mockBedrockClient, times(modelIds.length)).invokeModel(any(InvokeModelRequest.class));
    }

    @Test
    void invokeModel_shouldUseCorrectHttpHeaders() throws Exception {
        // Arrange
        String modelId = Models.CLAUDE_3_SONNET_MODEL_ID;
        ClaudePromptPayload payload = createTestPayload();
        String expectedResponse = Responses.GENERIC_JSON_RESPONSE;
        
        SdkBytes responseBytes = SdkBytes.fromString(expectedResponse, StandardCharsets.UTF_8);
        when(mockResponse.body()).thenReturn(responseBytes);
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

        // Act
        invoker.invokeModel(modelId, payload);

        // Assert - Verify correct HTTP headers are set
        verify(mockBedrockClient, times(Numbers.TIMES_ONE)).invokeModel(argThat((InvokeModelRequest request) -> {
            assertEquals(Models.CONTENT_TYPE_JSON, request.contentType());
            assertEquals(Models.ACCEPT_JSON, request.accept());
            return true;
        }));
    }

    /**
     * Helper method to create a standard test payload.
     */
    private ClaudePromptPayload createTestPayload() {
        return ClaudePromptPayload.builder()
                .anthropicVersion(Models.ANTHROPIC_VERSION)
                .maxTokens(Models.MAX_TOKENS_DEFAULT)
                .temperature(Models.TEMPERATURE_DEFAULT)
                .topK(Models.TOP_K_DEFAULT)
                .topP(Models.TOP_P_DEFAULT)
                .promptMessages(Collections.singletonList(
                    PromptMessage.builder()
                        .role(Models.USER_ROLE)
                        .content(Prompts.TEST_MESSAGE_CONTENT)
                        .build()
                ))
                .build();
    }
}
