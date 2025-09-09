package com.permitseoul.permitserver.domain.admin.event.api.dto.req;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminEventUpdateRequest(
        @Min(value = 1, message = "eventId가 1보다 작습니다.")
        long eventId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate eventExposureStartDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime eventExposureStartTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate eventExposureEndDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime eventExposureEndTime,

        @Size(max = 30, message = "검증 코드는 30자를 초과할 수 없습니다.")
        String verificationCode,
        String name,
        EventType eventType,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime startTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime endTime,
        String venue,
        String lineup,
        String details,
        List<AdminEventImageInfo> images,

        @Min(value = 0, message = "최소 나이는 0 이상이어야 합니다.")
        Integer minAge
) {
    public record AdminEventImageInfo(
            String imageUrl
    ) { }
}
