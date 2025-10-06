package com.permitseoul.permitserver.domain.admin.base.api.dto.req;

import com.permitseoul.permitserver.domain.admin.base.core.domain.MediaType;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record S3PreSignedUrlRequest(
        @Min(value = 1, message = "eventId는 1이상이여야 합니다.")
        long eventId,

        @NotNull(message = "eventType은 필수입니다.")
        EventType eventType,

        @NotNull(message = "mediaInfoRequests는 null일 수 없습니다.")
        @Valid
        List<MediaInfoRequest> mediaInfoRequests
) {
    public record MediaInfoRequest(
            @NotBlank(message = "mediaName은 필수입니다.")
            String mediaName,
            @NotNull(message = "mediaType은 필수입니다.")
            MediaType mediaType
    ) { }
}
