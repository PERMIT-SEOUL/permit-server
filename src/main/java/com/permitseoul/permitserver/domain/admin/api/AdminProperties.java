package com.permitseoul.permitserver.domain.admin.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(
        String accessCode
)  {
}
