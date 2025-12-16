package com.permitseoul.permitserver.domain.sitemapimage.api.exception;

import com.permitseoul.permitserver.domain.sitemapimage.SiteMapImageBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SiteMapImageApiException extends SiteMapImageBaseException {
    private final ErrorCode errorCode;

}
