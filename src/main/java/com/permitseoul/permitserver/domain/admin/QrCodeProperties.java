package com.permitseoul.permitserver.domain.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qr")
public record QrCodeProperties(
        String link
) {
}
