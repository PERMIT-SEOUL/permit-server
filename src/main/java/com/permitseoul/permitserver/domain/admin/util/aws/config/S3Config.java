package com.permitseoul.permitserver.domain.admin.util.aws.config;

import com.permitseoul.permitserver.domain.admin.util.aws.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final AwsS3Properties awsS3Properties;

    @Bean
    public StaticCredentialsProvider awsCredentialsProvider() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsS3Properties.accessKey(),
                awsS3Properties.secretKey()
        );
        return StaticCredentialsProvider.create(credentials);
    }

    @Bean
    public Region awsRegion() {
        return Region.of(awsS3Properties.region());
    }

    @Bean
    public S3Client s3Client(final StaticCredentialsProvider provider, final Region region) {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(provider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(final StaticCredentialsProvider provider, final Region region) {
        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(provider)
                .build();
    }
}
