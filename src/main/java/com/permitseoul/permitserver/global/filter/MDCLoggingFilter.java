package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.global.exception.FilterException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
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
import java.util.Objects;
import java.util.UUID;

import static java.util.Calendar.PM;

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

    @Override
    protected boolean shouldNotFilter(@NonNull final HttpServletRequest request) {
        return request.getRequestURI().startsWith(HEALTH_CHECK_URL);
    }
}
