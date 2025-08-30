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

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
class MDCLoggingFilter extends OncePerRequestFilter {
    private final static String NGINX_REQUEST_ID = "X-Request-ID";
    private final static String TRACE_ID = "trace_id";

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) {
        final String traceId = request.getHeader(NGINX_REQUEST_ID);
        log.info(traceId); //todo: 추후 삭제(테스트용)
        MDC.put(TRACE_ID, Objects.requireNonNullElse(traceId, UUID.randomUUID().toString().replaceAll("-", "")));
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new FilterException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.clear();
        }
    }
}
