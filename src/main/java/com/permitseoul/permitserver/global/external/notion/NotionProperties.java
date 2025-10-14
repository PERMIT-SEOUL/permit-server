package com.permitseoul.permitserver.global.external.notion;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notion")
public record NotionProperties(
        String privateApiToken,
        String notionVersion
) { }
