package com.permitseoul.permitserver.domain.admin.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotionImageUrlUtil {

    private static final String HTTPS = "https://";
    private static final String IMAGE_PATH = "/image/";
    private static final String QUERY_PREFIX = "?table=block&id=";
    private static final String CACHE_SUFFIX = "&cache=v2";

    public static String buildProxyUrl(final String publishedHost,
                                       final String pageId,
                                       final String originalUrl) {
        if (publishedHost == null || publishedHost.isBlank()) return null;
        if (pageId == null || pageId.isBlank()) return null;
        if (originalUrl == null || originalUrl.isBlank()) return null;

        // presigned query 제거하는 과정
        final String baseUrl = originalUrl.split("\\?")[0];
        return HTTPS + publishedHost
                + IMAGE_PATH + encodeURIComponent(baseUrl)
                + QUERY_PREFIX + pageId
                + CACHE_SUFFIX;
    }

    private static String encodeURIComponent(final String s) {
        String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return encoded.replace("+", "%20")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%7E", "~");
    }
}
