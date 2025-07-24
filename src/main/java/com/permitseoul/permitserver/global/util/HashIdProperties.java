package com.permitseoul.permitserver.global.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hashids")
public record HashIdProperties(
        String salt,
        int length
) {
}
