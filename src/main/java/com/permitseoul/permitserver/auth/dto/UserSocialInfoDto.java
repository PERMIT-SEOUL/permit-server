package com.permitseoul.permitserver.auth.dto;

import com.permitseoul.permitserver.user.domain.SocialType;

public record UserSocialInfoDto(
        SocialType socialType,
        Long userSocialId
) {
    public static UserSocialInfoDto of(final SocialType socialType, final Long userSocialId) {
        return new UserSocialInfoDto(socialType, userSocialId);
    }
}
