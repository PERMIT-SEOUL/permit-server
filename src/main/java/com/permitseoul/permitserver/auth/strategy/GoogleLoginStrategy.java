package com.permitseoul.permitserver.auth.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.auth.exception.AuthFeignException;
import com.permitseoul.permitserver.external.google.GoogleClient;
import com.permitseoul.permitserver.external.google.GoogleProperties;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.user.domain.SocialType;
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
                            Constants.AUTHCODE).id_token())
                    .filter(token -> !token.isBlank())
                    .orElseThrow(AuthFeignException::new);
            return UserSocialInfoDto.of(SocialType.GOOGLE, extractGoogleUid(googleIdToken), googleIdToken);
        } catch (Exception e) {
            throw new AuthFeignException();
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
        try {
            final String[] parts = idToken.split(ID_TOKEN_SPLIT);
            final String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            final ObjectMapper mapper = new ObjectMapper();
            final Map<String, Object> payloadMap = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            return (String) payloadMap.get(SUB);
        } catch (Exception e) {
            throw new AuthFeignException();
        }
    }
}
