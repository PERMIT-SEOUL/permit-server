package com.permitseoul.permitserver.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public final class HttpReqResLogJsonBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final int MAX_LENGTH = 1500;
    private static final String EMPTY_BODY = "(Empty Body)";
    private static final String OVER_MAX_LENGTH = "...more";
    private static final String TRACE_ID = "trace_id";
    private static final String NO_TRACE = "(no-trace)";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_LOG_TYPE = "log_type";
    private static final String FIELD_METHOD = "method";
    private static final String FIELD_URL = "url";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_DURATION = "duration_ms";
    private static final String FIELD_REQUEST_BODY = "request_body";
    private static final String FIELD_RESPONSE_BODY = "response_body";
    private static final String LOG_TYPE_HTTP = "HTTP";

    public static String buildJsonLog(final ContentCachingRequestWrapper request,
                                      final ContentCachingResponseWrapper response,
                                      final long duration) {
        try {
            final Map<String, Object> logMap = new LinkedHashMap<>();

            logMap.put(FIELD_TIME, LocalDateTime.now().toString());
            logMap.put(FIELD_LOG_TYPE, LOG_TYPE_HTTP);
            logMap.put(TRACE_ID, MDC.get(TRACE_ID) != null ? MDC.get(TRACE_ID) : NO_TRACE);
            logMap.put(FIELD_METHOD, request.getMethod());
            logMap.put(FIELD_URL, request.getRequestURI());
            logMap.put(FIELD_STATUS, response.getStatus());
            logMap.put(FIELD_DURATION, duration);
            logMap.put(FIELD_REQUEST_BODY, extractBody(request.getContentAsByteArray()));
            logMap.put(FIELD_RESPONSE_BODY, extractBody(response.getContentAsByteArray()));

            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(logMap);
        } catch (Exception e) {
            return "{\"error\":\"failed to format log\"}";
        }
    }

    private static String extractBody(byte[] arr) {
        if (arr == null || arr.length == 0) return EMPTY_BODY;
        final String body = new String(arr, StandardCharsets.UTF_8);
        return body.length() > MAX_LENGTH
                ? body.substring(0, MAX_LENGTH) + OVER_MAX_LENGTH
                : body;
    }
}
