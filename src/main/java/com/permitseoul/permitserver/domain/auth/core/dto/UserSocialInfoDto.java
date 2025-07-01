package com.permitseoul.permitserver.domain.auth.core.dto;

import com.permitseoul.permitserver.domain.user.core.domain.SocialType;

public record UserSocialInfoDto(
        SocialType socialType,
        String userSocialId,
        String socialAccessToken
) {
    public static UserSocialInfoDto of(final SocialType socialType, final String userSocialId, final String socialAccessToken) {
        return new UserSocialInfoDto(socialType, userSocialId, socialAccessToken);
    }
}
