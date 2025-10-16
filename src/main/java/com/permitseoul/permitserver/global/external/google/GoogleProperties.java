package com.permitseoul.permitserver.global.external.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.google")
public record GoogleProperties(
        String clientId,
        String clientSecretId
) {
}
