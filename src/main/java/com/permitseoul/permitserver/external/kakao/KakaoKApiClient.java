package com.permitseoul.permitserver.external.kakao;

import com.permitseoul.permitserver.external.kakao.dto.KakaoATInfoResponse;
import com.permitseoul.permitserver.global.Constants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@EnableConfigurationProperties(KakaoProperties.class)
@FeignClient(name = "KakaoKApiClient", url = "https://kapi.kakao.com")
public interface KakaoKApiClient {
    @GetMapping("/access_token_info")
    KakaoATInfoResponse getKakaoSocialId(@RequestHeader(Constants.AUTHORIZATION) final String accessTokenWithBearer);

}
