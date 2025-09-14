package com.permitseoul.permitserver.global;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hashids")
public record HashIdProperties(
        String salt,
        int length
) {
}
