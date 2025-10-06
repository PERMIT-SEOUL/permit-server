package com.permitseoul.permitserver.domain.admin.util.aws;

import com.permitseoul.permitserver.domain.admin.base.api.dto.req.S3PreSignedUrlRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.res.S3PreSignedUrlResponse;
import com.permitseoul.permitserver.domain.admin.base.core.domain.MediaType;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final AwsS3Properties awsS3Properties;

    private static final long EXPIRE_TIME = 10; // pre-signed url 유효기간(10분)

    public S3PreSignedUrlResponse getS3PreSignedUrls(final long eventId,
                                                     final List<S3PreSignedUrlRequest.MediaInfoRequest> mediaInfoRequests) {
        final List<S3PreSignedUrlResponse.PreSignedUrlInfo> preSignedUrlInfo = mediaInfoRequests.stream()
                .map(mediaInfoRequest -> {
                            return S3PreSignedUrlResponse.PreSignedUrlInfo.of(
                                    generatePreSignedUrl(eventId, mediaInfoRequest.mediaType()), mediaInfoRequest.mediaName()
                            );
                        }
                ).toList();

        return S3PreSignedUrlResponse.of(preSignedUrlInfo);
    }

    private String generatePreSignedUrl(final long eventId,
                                        final MediaType mediaType) {
        final String fileName = generateFileName(); //uuid
        final StringBuilder key = new StringBuilder();

        //경로 : events/{eventType}/{eventId}/{mediaType}/{filename}
        key.append("events/").append(eventId).append("/").append(mediaType.toString()).append("/").append(fileName);

        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.bucket())
                .key(String.valueOf(key))
                .build();
        final PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRE_TIME))
                .putObjectRequest(putObjectRequest)
                .build();
        final URL preSignedUrl = s3Presigner.presignPutObject(preSignRequest).url();

        return preSignedUrl.toString();
    }

    private String generateFileName() {
        return UUID.randomUUID().toString();
    }
}
