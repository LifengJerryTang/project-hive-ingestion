package com.projecthive.summarization.constants;

public final class PromptConstants {
    private PromptConstants() {} // no instantiation

    /** Anthropic / Bedrock prompt version **/
    public static final String ANTHROPIC_VERSION_VALUE = "bedrock-2023-05-31";

    /** JSON property names for prompts **/
    public static final String JSON_ANTHROPIC_VERSION = "anthropic_version";
    public static final String JSON_MAX_TOKENS        = "max_tokens";
    public static final String JSON_TEMPERATURE       = "temperature";
    public static final String JSON_TOP_K             = "top_k";
    public static final String JSON_TOP_P             = "top_p";
    public static final String JSON_MESSAGES          = "messages";

    /** JSON property names for individual message objects **/
    public static final String JSON_MESSAGE_ROLE      = "role";
    public static final String JSON_MESSAGE_CONTENT   = "content";

    public static final String USER_ROLE = "user";
}
