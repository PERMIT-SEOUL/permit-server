package com.permitseoul.permitserver.domain.admin.util.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {
    private String accessKey;
    private String secretKey;
    private String bucket;
}
