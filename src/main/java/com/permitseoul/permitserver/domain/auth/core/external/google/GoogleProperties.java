package com.permitseoul.permitserver.domain.auth.core.external.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.google")
public record GoogleProperties(
        String clientId,
        String clientSecretId
) {
}
