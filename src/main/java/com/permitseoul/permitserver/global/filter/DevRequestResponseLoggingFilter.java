package com.permitseoul.permitserver.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.global.external.discord.DiscordSender;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component //스프링이 자동 등록 (SecurityFilterChain보다 앞단에서 동작)
@Profile("dev") // dev 환경에서만 활성화
@RequiredArgsConstructor
public class DevRequestResponseLoggingFilter implements Filter {

    private final DiscordSender discordSender; // ✅ 비동기 Discord 전송용 컴포넌트

    private static final int MAX_LENGTH = 1500;
    private static final int ZERO = 0;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String FIELD_TIME = "time";
    private static final String FIELD_LOG_TYPE = "log_type";
    private static final String FIELD_METHOD = "method";
    private static final String FIELD_URL = "url";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_DURATION = "duration_ms";
    private static final String FIELD_REQUEST_BODY = "request_body";
    private static final String FIELD_RESPONSE_BODY = "response_body";
    private static final String HTTP = "HTTP";
    private static final String TRACE_ID = "trace_id";
    private static final String EMPTY_BODY = "(Empty Body)";
    private static final String OVER_MAX_LENGTH = "...more";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 요청/응답을 여러 번 읽을 수 있도록 래핑
        final ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        final ContentCachingResponseWrapper res = new ContentCachingResponseWrapper((HttpServletResponse) response);

        final long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            final long duration = System.currentTimeMillis() - start;
            logRequestResponse(req, res, duration);
            res.copyBodyToResponse(); // 응답 body 복사
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    long duration) {
        try {
            final Map<String, Object> logMap = new LinkedHashMap<>();
            final String traceId = MDC.get("trace_id") != null ? MDC.get("trace_id") : "(no-trace)";

            logMap.put(FIELD_TIME, LocalDateTime.now().toString());
            logMap.put(FIELD_LOG_TYPE, HTTP);
            logMap.put(TRACE_ID, traceId);
            logMap.put(FIELD_METHOD, request.getMethod());
            logMap.put(FIELD_URL, request.getRequestURI());
            logMap.put(FIELD_STATUS, response.getStatus());
            logMap.put(FIELD_DURATION, duration);
            logMap.put(FIELD_REQUEST_BODY, extractBody(request.getContentAsByteArray()));
            logMap.put(FIELD_RESPONSE_BODY, extractBody(response.getContentAsByteArray()));

            final String jsonLog = OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(logMap);

            discordSender.send(jsonLog);

        } catch (Exception e) {
            log.error("Error while logging request/response", e);
        }
    }

    private String extractBody(byte[] arr) {
        if (arr == null || arr.length == ZERO) return EMPTY_BODY;
        final String body = new String(arr, StandardCharsets.UTF_8);
        return body.length() > MAX_LENGTH
                ? body.substring(ZERO, MAX_LENGTH) + OVER_MAX_LENGTH
                : body;
    }
}
