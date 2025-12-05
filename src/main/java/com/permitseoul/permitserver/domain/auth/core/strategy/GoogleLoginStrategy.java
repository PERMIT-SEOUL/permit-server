package com.permitseoul.permitserver.domain.auth.core.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthFeignException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthPlatformFeignException;
import com.permitseoul.permitserver.global.external.google.client.GoogleClient;
import com.permitseoul.permitserver.global.external.google.GoogleProperties;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GoogleLoginStrategy implements LoginStrategy {
    private final GoogleClient googleClient;
    private final GoogleProperties googleProperties;
    private static final String ID_TOKEN_SPLIT = "\\.";
    private static final String SUB = "sub";

    @Override
    public UserSocialInfoDto getUserSocialInfo(final String authorizationCode, final String redirectUrl) {
        try {
            final String googleIdToken = Optional.ofNullable(googleClient.getGoogleToken(
                            authorizationCode,
                            googleProperties.clientId(),
                            googleProperties.clientSecretId(),
                            redirectUrl,
                            Constants.AUTHCODE).idToken())
                    .filter(token -> !token.isBlank())
                    .orElseThrow(AuthFeignException::new);
            return UserSocialInfoDto.of(SocialType.GOOGLE, extractGoogleUid(googleIdToken), googleIdToken);
        } catch (FeignException e) {
            throw new AuthPlatformFeignException(e.contentUTF8());
        }
    }

    @Override
    public String getUserSocialId(String socialAccessToken) {
        return getGoogleSocialId(socialAccessToken);
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }

    private String getGoogleSocialId(String socialAccessToken) {
        return extractGoogleUid(socialAccessToken);
    }

    private String extractGoogleUid(final String idToken) {

        final String[] parts = idToken.split(ID_TOKEN_SPLIT);
        final String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> payloadMap;
        try {
            payloadMap = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new AuthFeignException();
        }
        return (String) payloadMap.get(SUB);

    }
}
