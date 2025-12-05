package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.global.external.discord.DiscordSender;
import com.permitseoul.permitserver.global.util.HttpReqResLogJsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevRequestResponseLoggingFilter implements Filter {

    private final DiscordSender discordSender;

    private final static String HEALTH_CHECK_URL = "/actuator/health";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        final ContentCachingResponseWrapper res = new ContentCachingResponseWrapper((HttpServletResponse) response);

        Exception exception = null;

        final long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            exception = e;
            log.error("🚨 [DevRequestResponseLoggingFilter] 요청 중 예외 발생: {}", e.getMessage(), e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        finally {
            final long duration = System.currentTimeMillis() - start;
            final String uri = req.getRequestURI();
            if (!uri.startsWith(HEALTH_CHECK_URL)) {
                final String jsonLog = HttpReqResLogJsonBuilder.buildJsonLog(req, res, duration, exception);
                discordSender.send(jsonLog);
            }
            res.copyBodyToResponse();
        }

        if (exception != null) {
            if (exception instanceof ServletException se) throw se;
            if (exception instanceof IOException ioe) throw ioe;
            throw new ServletException(exception);
        }
    }
}
