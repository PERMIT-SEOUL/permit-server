package com.permitseoul.permitserver.auth.dto;

import com.permitseoul.permitserver.user.domain.Sex;
import com.permitseoul.permitserver.user.domain.SocialType;

public record SignUpRequest(
        String userName,
        int userAge,
        Sex userSex,
        String userEmail,
        SocialType socialType,
        String authorizationCode,
        String redirectUrl
) {
}
