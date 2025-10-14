package com.permitseoul.permitserver.global.external.kakao.client;

import com.permitseoul.permitserver.global.external.kakao.dto.KakaoAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoKAuthClient", url = "https://kauth.kakao.com")
public interface KakaoKAuthClient {

    @PostMapping("/oauth/token")
    KakaoAccessTokenResponse getKakaoAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
}
