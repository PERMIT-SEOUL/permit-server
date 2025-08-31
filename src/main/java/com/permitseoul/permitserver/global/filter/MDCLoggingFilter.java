package com.permitseoul.permitserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
class MDCLoggingFilter extends OncePerRequestFilter {
    private final static String NGINX_REQUEST_ID = "X-Request-ID";
    private final static String TRACE_ID = "trace_id";
    private final static String HEALTH_CHECK_URL = "/actuator/health";

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        final String uri = request.getRequestURI();
        if (uri != null && uri.contains(HEALTH_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String traceId = request.getHeader(NGINX_REQUEST_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString().replaceAll("-", "");
            }
            MDC.put(TRACE_ID, traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
