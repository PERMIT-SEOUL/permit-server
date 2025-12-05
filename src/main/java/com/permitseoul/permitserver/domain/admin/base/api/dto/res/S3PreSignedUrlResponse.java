package com.permitseoul.permitserver.domain.admin.base.api.dto.res;

import java.util.List;

public record S3PreSignedUrlResponse(
        List<PreSignedUrlInfo> preSignedUrlInfoList
) {
    public static S3PreSignedUrlResponse of(final List<PreSignedUrlInfo> preSignedUrlInfoList) {
            return new S3PreSignedUrlResponse(preSignedUrlInfoList);
    }

    public record PreSignedUrlInfo(
            String preSignedUrl,
            String mediaName
    ) {
        public static PreSignedUrlInfo of(final String preSignedUrl, final String mediaName) {
            return new PreSignedUrlInfo(preSignedUrl, mediaName);
        }
    }
}
