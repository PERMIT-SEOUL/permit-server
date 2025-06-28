package com.permitseoul.permitserver.external.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.kakao")
public record KakaoProperties(
        String clientId
) {
}
