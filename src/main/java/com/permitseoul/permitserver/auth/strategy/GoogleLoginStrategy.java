package com.permitseoul.permitserver.auth.strategy;

import com.permitseoul.permitserver.auth.dto.CookieDto;
import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.user.domain.SocialType;

public class GoogleLoginStrategy implements LoginStrategy {

    @Override
    public UserSocialInfoDto getUserSocialInfo(final String authorizationCode) {
        return null;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }
}
