package com.permitseoul.permitserver.domain.sitemapimage.api;

import com.permitseoul.permitserver.domain.sitemapimage.api.exception.SiteMapImageApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.sitemapimage")
public class EventSiteMapExceptionHandler {

    @ExceptionHandler(SiteMapImageApiException.class)
    public ResponseEntity<BaseResponse<?>> handleSiteMapImageApiException(final SiteMapImageApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }
}
