package com.permitseoul.permitserver.domain.reservation.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossProperties(
        String apiSecretKey
) {
}
