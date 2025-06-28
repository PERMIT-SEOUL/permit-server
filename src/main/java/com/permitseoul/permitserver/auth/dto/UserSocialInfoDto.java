package com.permitseoul.permitserver.auth.dto;

import com.permitseoul.permitserver.user.domain.SocialType;

public record UserSocialInfoDto(
        SocialType socialType,
        String userSocialId,
        String socialAccessToken
) {
    public static UserSocialInfoDto of(final SocialType socialType, final String userSocialId, final String socialAccessToken) {
        return new UserSocialInfoDto(socialType, userSocialId, socialAccessToken);
    }
}
