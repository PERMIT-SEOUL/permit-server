package com.permitseoul.permitserver.auth.strategy;

import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.external.kakao.KakaoKApiClient;
import com.permitseoul.permitserver.external.kakao.KakaoKAuthClient;
import com.permitseoul.permitserver.external.kakao.KakaoProperties;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.user.domain.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoLoginStrategy implements LoginStrategy {
    private final KakaoKAuthClient kakaoKAuthClient;
    private final KakaoProperties kakaoProperties;
    private final KakaoKApiClient kakaoKApiClient;

    @Override
    public UserSocialInfoDto getUserSocialInfo(final String authorizationCode, final String redirectUrl) {
        final String kakaoAccessToken = getKakaoAccessToken(authorizationCode, redirectUrl);
        return UserSocialInfoDto.of(SocialType.KAKAO, getKakaoSocialId(kakaoAccessToken));
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    private String getKakaoAccessToken(final String authorizationCode, final String redirectUrl) {
        return kakaoKAuthClient.getKakaoAccessToken(
                Constants.KAKAO_AUTHCODE,
                kakaoProperties.clientId(),
                redirectUrl,
                authorizationCode
        ).accessToken();
    }

    private Long getKakaoSocialId(final String kakaoAccessToken) {
        return kakaoKApiClient.getKakaoSocialId(Constants.BEARER + kakaoAccessToken).id();
    }
}
