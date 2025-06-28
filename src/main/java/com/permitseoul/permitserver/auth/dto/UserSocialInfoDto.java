package com.permitseoul.permitserver.auth.dto;

import com.permitseoul.permitserver.user.domain.SocialType;

public record UserSocialInfoDto(
        SocialType socialType,
        String userSocialId
) {
    public static UserSocialInfoDto of(final SocialType socialType, final String userSocialId) {
        return new UserSocialInfoDto(socialType, userSocialId);
    }
}
