package com.permitseoul.permitserver.global.external.discord.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class DiscordMessageFormatterUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String FIELD_TIME = "time";
    private static final String FIELD_LOG_TYPE = "log_type";
    private static final String FIELD_METHOD = "method";
    private static final String FIELD_URL = "url";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_TRACE_ID = "trace_id";
    private static final String FIELD_DURATION = "duration_ms";
    private static final String FIELD_REQUEST_BODY = "request_body";
    private static final String FIELD_RESPONSE_BODY = "response_body";
    private static final String FIELD_EXCEPTION = "exception";
    private static final String FIELD_STACKTRACE = "stacktrace";
    private static final String DEFAULT_NO_TIME = "(no time)";
    private static final String DEFAULT_NO_LOG_TYPE = "N/A";
    private static final String DEFAULT_NO_METHOD = "UNKNOWN";
    private static final String DEFAULT_NO_URL = "N/A";
    private static final String DEFAULT_NO_TRACE_ID = "(no-trace)";
    private static final long DEFAULT_DURATION = 0L;
    private static final String EMPTY_BODY = "(Empty Body)";
    private static final int DEFAULT_NO_STATUS = 0;

    // [dev 환경] HTTP 요청/응답 로그 포맷팅
    public static String formatDevHttpLogToMarkdown(final JsonNode root) {
        final String time = root.path(FIELD_TIME).asText(DEFAULT_NO_TIME);
        final String logType = root.path(FIELD_LOG_TYPE).asText(DEFAULT_NO_LOG_TYPE);
        final String method = root.path(FIELD_METHOD).asText(DEFAULT_NO_METHOD);
        final String url = root.path(FIELD_URL).asText(DEFAULT_NO_URL);
        final int status = root.path(FIELD_STATUS).asInt(DEFAULT_NO_STATUS);
        final String traceId = root.path(FIELD_TRACE_ID).asText(DEFAULT_NO_TRACE_ID);
        final long duration = root.path(FIELD_DURATION).asLong(DEFAULT_DURATION);

        final String requestBody = prettyJson(root.path(FIELD_REQUEST_BODY).asText());
        final String responseBody = prettyJson(root.path(FIELD_RESPONSE_BODY).asText());

        final String exception = root.path(FIELD_EXCEPTION).isMissingNode()
                ? null
                : root.path(FIELD_EXCEPTION).asText(null);

        final String stacktrace = root.path(FIELD_STACKTRACE).isMissingNode()
                ? null
                : root.path(FIELD_STACKTRACE).asText(null);

        String exceptionSection = "";
        if (exception != null && !exception.isBlank()) {
            exceptionSection = """
                
                **Exception:**
                ```text
                %s
                ```
                """.formatted(exception);
        }

        String stacktraceSection = "";
        if (stacktrace != null && !stacktrace.isBlank()) {
            stacktraceSection = """
                
                **Stacktrace:**
                ```text
                %s
                ```
                """.formatted(stacktrace);
        }

        return """
        **[HTTP 요청/응답 로그]**

        🕓 **time:** %s
        🧩 **log_type:** %s
        🧭 **method:** %s
        🌐 **url:** %s
        📦 **status:** %d
        🫆 **trace_id:** `%s`
        ⏱️ **duration:** %dms

        **Request Body:**
        ```json
        %s
        ```
        **Response Body:**
        ```json
        %s
        ```%s%s
        """.formatted(
                time, logType, method, url, status, traceId, duration,
                requestBody, responseBody,
                exceptionSection,       // %s
                stacktraceSection       // %s
        );
    }

    public static String prettyJson(final String body) {
        if (body == null || body.isBlank() || body.equals(EMPTY_BODY)) {
            return EMPTY_BODY;
        }
        try {
            final JsonNode node = OBJECT_MAPPER.readTree(body);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return body;
        }
    }
}
