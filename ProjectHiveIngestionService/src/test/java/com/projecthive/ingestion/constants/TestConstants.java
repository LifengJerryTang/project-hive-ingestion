package com.projecthive.ingestion.constants;

public class TestConstants {

    public static final String USER_ID = "me";
    public static final String QUERY_UNREAD = "is:unread";
    public static final String MSG_ID_1 = "msg1";
    public static final String MSG_ID_2 = "msg2";
    public static final String SNIPPET_1 = "hello";
    public static final String SNIPPET_2 = "world";
    public static final String ERROR_MESSAGE = "API error";
    public static final String ID = "id";
    public static final String SECRET = "secret";
    public static final String REFRESH = "refresh";

    public static final String SENDER_1 = "from1@example.com";
    public static final String RECEIVER_1 = "to1@example.com";
    public static final String SUBJECT_1 = "Subject 1";
    public static final String USERNAME = "username";
    public static final long RECEIVED_AT_1 = 1690000000000L;

    public static final String SENDER_2 = "from2@example.com";
    public static final String RECEIVER_2 = "to2@example.com";
    public static final String SUBJECT_2 = "Subject 2";

    public static final String MESSAGES_TABLE = "messages";

    public static final String EVENT_ID = "test-event-id";
    public static final String DETAIL_TYPE = "test-detail-type";

    private TestConstants() {
        // Prevent instantiation
    }
}
