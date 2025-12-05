package com.permitseoul.permitserver.domain.admin.util.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsS3Properties (
        String accessKey,
        String secretKey,
        String bucket,
        String region
){ }
