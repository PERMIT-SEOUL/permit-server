package com.permitseoul.permitserver.domain.admin.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record EmailProperties(
        String sender
) {
}
