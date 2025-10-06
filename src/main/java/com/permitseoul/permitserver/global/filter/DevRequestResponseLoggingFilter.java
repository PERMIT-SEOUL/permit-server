package com.permitseoul.permitserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component // 필터 가장 처음
@Profile("dev") // dev에서만 적용(테스트 및 qa용)
public class DevRequestResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 요청 응답을 여러 번 읽을 수 있도록 래핑
        final ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        final ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        final long start = System.currentTimeMillis();
        chain.doFilter(reqWrapper, resWrapper);
        final long duration = System.currentTimeMillis() - start;

        logRequestResponse(reqWrapper, resWrapper, duration);
        resWrapper.copyBodyToResponse();
    }

    private void logRequestResponse(final ContentCachingRequestWrapper request,
                                    final ContentCachingResponseWrapper response,
                                    final long duration) {
        try {
            final String method = request.getMethod();
            final String uri = request.getRequestURI();
            final String query = request.getQueryString();
            final String fullUrl = uri + (query != null ? "?" + query : "");

            final String reqBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            final String resBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("""
                    🧩 [HTTP LOG]
                    ▶️ Time: {}
                    ▶️ Method: {}
                    ▶️ URL: {}
                    ▶️ Status: {}
                    ▶️ Duration: {} ms
                    ▶️ Request Body: {}
                    ▶️ Response Body: {}
                    """,
                    LocalDateTime.now(), method, fullUrl,
                    response.getStatus(), duration, sanitize(reqBody), sanitize(resBody)
            );

        } catch (Exception e) {
            log.error("Error logging request/response", e);
        }
    }

    private String sanitize(String input) {
        if (input == null || input.isBlank()) return "(empty Body)";
        // 필요 시 개인정보 마스킹 처리
        return input.length() > 2000 ? input.substring(0, 2000) + "...(truncated)" : input; //2000자까지만 보임
    }
}