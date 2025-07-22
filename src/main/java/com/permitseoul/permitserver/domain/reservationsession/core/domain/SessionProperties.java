package com.permitseoul.permitserver.domain.reservationsession.core.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "session.reservation")
public record SessionProperties(
        long expireTime
) {
}
