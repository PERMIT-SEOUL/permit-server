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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 요청,응답을 여러 번 읽을 수 있도록 래핑
        final ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        final ContentCachingResponseWrapper res = new ContentCachingResponseWrapper((HttpServletResponse) response);

        final long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            final long duration = System.currentTimeMillis() - start;
            final String jsonLog = HttpReqResLogJsonBuilder.buildJsonLog(req, res, duration);
            discordSender.send(jsonLog);
            res.copyBodyToResponse();
        }
    }
}
