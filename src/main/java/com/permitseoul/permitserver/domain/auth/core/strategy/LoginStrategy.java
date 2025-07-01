package com.permitseoul.permitserver.domain.auth.core.strategy;

import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;

public interface LoginStrategy {
    UserSocialInfoDto getUserSocialInfo(final String authorizationCode, final String redirectUrl);
    String getUserSocialId(final String socialAccessToken);
    SocialType getSocialType();

}
