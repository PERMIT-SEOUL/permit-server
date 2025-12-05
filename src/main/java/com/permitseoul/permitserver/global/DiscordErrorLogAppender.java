package com.permitseoul.permitserver.global;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DiscordErrorLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String url;

    private int connectTimeout = 3000;

    private int readTimeout = 3000;

    private static final DateTimeFormatter KST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.of("Asia/Seoul"));

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    protected void append(final ILoggingEvent event) {
        if (!isStarted() || url == null || url.isBlank()) {
            return;
        }

        try {
            if (!event.getLevel().isGreaterOrEqual(Level.ERROR)) {
                return;
            }

            final String content = buildContent(event);
            if (content == null || content.isBlank()) {
                return;
            }

            final String jsonBody = """
                    {
                      "content": "%s"
                    }
                    """.formatted(escapeForJson(content));

            send(jsonBody);

        } catch (final Exception e) {
            addError("Failed to send error log to Discord", e);
        }
    }

    private String buildContent(final ILoggingEvent event) {
        final Map<String, String> mdc = event.getMDCPropertyMap();

        final String traceId = safe(mdc.get("trace_id"));
        final String uri = safe(mdc.get("uri"));
        final String method = safe(mdc.get("method"));
        final String status = safe(mdc.get("status"));

        final String timestamp = KST_FORMATTER.format(Instant.ofEpochMilli(event.getTimeStamp()));

        final StringBuilder sb = new StringBuilder();

        sb.append("[PERMIT-PROD] ")
                .append(event.getLevel())
                .append(" at ")
                .append(timestamp)
                .append(" (Asia/Seoul)")
                .append("\n");

        sb.append("logger=").append(event.getLoggerName())
                .append(" | thread=").append(event.getThreadName())
                .append("\n");

        sb.append("trace_id=").append(traceId)
                .append(" | uri=").append(uri)
                .append(" | method=").append(method)
                .append(" | status=").append(status)
                .append("\n\n");

        sb.append("message: ")
                .append(event.getFormattedMessage());

        final IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            String stackTrace = ThrowableProxyUtil.asString(throwableProxy);

            final int maxStackLength = 1700;
            if (stackTrace.length() > maxStackLength) {
                stackTrace = stackTrace.substring(0, maxStackLength) + "\n... (truncated)";
            }

            sb.append("\n\n")
                    .append("```")
                    .append("\n")
                    .append(stackTrace)
                    .append("\n```");
        }

        return sb.toString();
    }

    private void send(final String body) throws Exception {
        HttpURLConnection conn = null;
        try {
            final URL webhookUrl = new URL(url);
            conn = (HttpURLConnection) webhookUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(bytes.length);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
                os.flush();
            }

            final int statusCode = conn.getResponseCode();
            if (statusCode / 100 != 2) {
                addWarn("Discord webhook responded with status: " + statusCode);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String safe(final String value) {
        return value == null ? "" : value;
    }

    private String escapeForJson(final String content) {
        return content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
