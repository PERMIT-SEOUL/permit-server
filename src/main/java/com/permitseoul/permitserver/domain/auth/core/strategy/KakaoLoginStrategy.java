package com.permitseoul.permitserver.domain.auth.core.strategy;

import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthFeignException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthPlatformFeignException;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.KakaoKApiClient;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.KakaoKAuthClient;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.KakaoProperties;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.dto.KakaoAccessTokenResponse;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class KakaoLoginStrategy implements LoginStrategy {
    private final KakaoKAuthClient kakaoKAuthClient;
    private final KakaoProperties kakaoProperties;
    private final KakaoKApiClient kakaoKApiClient;

    @Override
    public UserSocialInfoDto getUserSocialInfo(final String authorizationCode, final String redirectUrl) {
        try {
            final String kakaoAccessToken = getKakaoAccessToken(authorizationCode, redirectUrl);
            return UserSocialInfoDto.of(SocialType.KAKAO, getKakaoSocialId(kakaoAccessToken), kakaoAccessToken);
        } catch (FeignException e) {
            throw new AuthPlatformFeignException(e.contentUTF8());
        }
    }

    @Override
    public String getUserSocialId(String socialAccessToken) {
        return getKakaoSocialId(socialAccessToken);
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    private String getKakaoAccessToken(final String authorizationCode, final String redirectUrl) {
        return Optional.ofNullable(kakaoKAuthClient.getKakaoAccessToken(
                        Constants.AUTHCODE,
                        kakaoProperties.clientId(),
                        redirectUrl,
                        authorizationCode))
                .map(KakaoAccessTokenResponse::accessToken)
                .filter(token -> !token.isBlank())
                .orElseThrow(AuthFeignException::new);
    }

    private String getKakaoSocialId(final String kakaoAccessToken) {
        return Optional.ofNullable(kakaoKApiClient.getKakaoSocialId(Constants.BEARER + kakaoAccessToken).id())
                .map(Objects::toString)
                .orElseThrow(AuthFeignException::new);
    }
}
