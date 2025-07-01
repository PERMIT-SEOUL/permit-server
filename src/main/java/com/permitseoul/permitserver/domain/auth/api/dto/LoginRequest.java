package com.permitseoul.permitserver.domain.auth.api.dto;

import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "소셜 타입은 필수입니다.")
        SocialType socialType,

        @NotBlank(message = "authorizationCode는 필수입니다.")
        String authorizationCode,

        @NotBlank(message = "redirectUrl은 필수입니다.")
        String redirectUrl
) {
}
