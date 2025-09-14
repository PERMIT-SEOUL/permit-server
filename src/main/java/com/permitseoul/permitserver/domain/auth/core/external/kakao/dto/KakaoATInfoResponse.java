package com.permitseoul.permitserver.domain.auth.core.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoATInfoResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("app_id") Integer appId
) {
}
