package com.permitseoul.permitserver.auth.strategy;

import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.user.domain.SocialType;

public interface LoginStrategy {
    UserSocialInfoDto getUserSocialInfo(final String authorizationCode, final String redirectUrl);
    SocialType getSocialType();
}
