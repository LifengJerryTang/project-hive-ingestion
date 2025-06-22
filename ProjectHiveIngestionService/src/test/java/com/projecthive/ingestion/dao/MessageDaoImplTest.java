package com.projecthive.ingestion.dao;

import com.projecthive.ingestion.constants.TestConstants;
import com.projecthive.ingestion.exceptions.DaoDataAccessException;
import com.projecthive.ingestion.models.Message;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static com.projecthive.ingestion.constants.CommonConstants.GMAIL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageDaoImplTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private DynamoDbEnhancedClient mockEnhancedClient;

    @Mock
    private DynamoDbTable<Message> mockTable;

    private MessageDaoImpl messageDao;

    @BeforeEach
    public void setUp() {
        when(mockEnhancedClient.table(eq(TestConstants.MESSAGES_TABLE), any(TableSchema.class)))
                .thenReturn(mockTable);

        messageDao = new MessageDaoImpl(mockDynamoDbClient) {
            @Override
            protected DynamoDbEnhancedClient buildEnhancedClient(@NonNull final DynamoDbClient client) {
                return mockEnhancedClient;
            }
        };
    }

    @Test
    public void testSave_callsPutItem() {
        final Message testMessage = Message.builder()
                .platform(GMAIL)
                .id(TestConstants.ID)
                .username(TestConstants.USERNAME)
                .platformMessageId(TestConstants.MSG_ID_1)
                .recipient(TestConstants.RECEIVER_1)
                .sender(TestConstants.SENDER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        messageDao.save(testMessage);

        verify(mockTable).putItem(any(PutItemEnhancedRequest.class));
    }

    @Test
    public void testSave_whenPutFails_throwsDaoDataAccessException() {
        final Message testMessage = Message.builder()
                .id(TestConstants.ID)
                .username(TestConstants.USERNAME)
                .platform(GMAIL)
                .platformMessageId(TestConstants.MSG_ID_1)
                .recipient(TestConstants.RECEIVER_1)
                .sender(TestConstants.SENDER_1)
                .subject(TestConstants.SUBJECT_1)
                .body(TestConstants.SNIPPET_1)
                .receivedAt(TestConstants.RECEIVED_AT_1)
                .build();

        doThrow(RuntimeException.class)
                .when(mockTable)
                .putItem(any(PutItemEnhancedRequest.class));

        assertThrows(DaoDataAccessException.class, () -> messageDao.save(testMessage));
    }
    
}
