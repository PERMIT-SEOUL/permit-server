package com.permitseoul.permitserver.auth.dto;

import com.permitseoul.permitserver.user.domain.SocialType;

public record LoginRequest(
        SocialType socialType,
        String authorizationCode
) {
}
