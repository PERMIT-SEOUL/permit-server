package com.permitseoul.permitserver.global.external.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.kakao")
public record KakaoProperties(
        String clientId
) {
}
