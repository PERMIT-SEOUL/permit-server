package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.global.Constants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@Profile("!local")
@Order(Ordered.HIGHEST_PRECEDENCE)
class MDCLoggingFilter extends OncePerRequestFilter {
    private static final String NGINX_REQUEST_ID = "X-Request-ID";
    private static final String TRACE_ID = "trace_id";

    private static final String URI = "uri";
    private static final String METHOD = "method";
    private static final String STATUS = "status";

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        final String uri = request.getRequestURI();
        // 헬스체크는 패스
        if (uri != null && uri.contains(Constants.HEALTH_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String traceId = request.getHeader(NGINX_REQUEST_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString().replaceAll("-", "");
            }
            MDC.put(TRACE_ID, traceId);
            MDC.put(URI, request.getRequestURI());
            MDC.put(METHOD, request.getMethod());

            filterChain.doFilter(request, response);

            MDC.put(STATUS, String.valueOf(response.getStatus()));
        } finally {
            MDC.remove(STATUS);
            MDC.remove(METHOD);
            MDC.remove(URI);
            MDC.remove(TRACE_ID);
        }
    }
}
