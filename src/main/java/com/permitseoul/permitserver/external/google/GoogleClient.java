package com.permitseoul.permitserver.external.google;

import com.permitseoul.permitserver.external.google.dto.GoogleTokenResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@EnableConfigurationProperties(GoogleProperties.class)
@FeignClient(name = "GoogleClient", url = "https://oauth2.googleapis.com")
public interface GoogleClient {

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    GoogleTokenResponse getGoogleToken(
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("grant_type") String grantType
            );
}
