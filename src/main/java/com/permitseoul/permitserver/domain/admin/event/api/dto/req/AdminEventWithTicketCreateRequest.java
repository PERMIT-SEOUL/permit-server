package com.permitseoul.permitserver.domain.admin.event.api.dto.req;

import com.permitseoul.permitserver.domain.admin.event.api.dto.res.AdminEventDetailResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record AdminEventWithTicketCreateRequest(

        @NotBlank(message = "행사 노출 시작일은 필수입니다.")
        String eventExposureStartDate,

        @NotBlank(message = "행사 노출 시작 시간은 필수입니다.")
        String eventExposureStartTime,

        @NotBlank(message = "행사 노출 종료일은 필수입니다.")
        String eventExposureEndDate,

        @NotBlank(message = "행사 노출 종료 시간은 필수입니다.")
        String eventExposureEndTime,

        @NotBlank(message = "검증 코드는 필수입니다.")
        @Size(max = 30, message = "검증 코드는 30자를 초과할 수 없습니다.")
        String verificationCode,

        @NotBlank(message = "행사명은 필수입니다.")
        String name,

        @NotBlank(message = "행사 시작일은 필수입니다.")
        String startDate,

        @NotBlank(message = "행사 시작 시간은 필수입니다.")
        String startTime,

        @NotBlank(message = "행사 종료일은 필수입니다.")
        String endDate,

        @NotBlank(message = "행사 종료 시간은 필수입니다.")
        String endTime,

        @NotBlank(message = "행사 장소는 필수입니다.")
        String venue,
        String lineup,
        String details,

        @NotEmpty(message = "행사 이미지는 최소 1개 이상이어야 합니다.")
        List<AdminEventImageInfo> images,

        @NotNull(message = "최소 나이는 필수입니다.")
        int minAge,

        @NotBlank(message = "티켓 차수 이름은 필수입니다.")
        String ticketRoundName,

        @NotBlank(message = "티켓 차수 시작일은 필수입니다.")
        String roundExposureStartDate,

        @NotBlank(message = "티켓 차수 시작 시간은 필수입니다.")
        String roundExposureStartTime,

        @NotBlank(message = "티켓 차수 종료일은 필수입니다.")
        String roundExposureEndDate,

        @NotBlank(message = "티켓 차수 종료 시간은 필수입니다.")
        String roundExposureEndTime,

        @NotEmpty(message = "티켓 정보는 최소 1개 이상이어야 합니다.")
        @Valid
        List<TicketTypeRequest> ticketTypes
) {
        public record TicketTypeRequest(
                @NotBlank(message = "티켓 이름은 필수입니다.")
                String ticketName,

                @NotNull(message = "가격은 필수입니다.")
                BigDecimal price,

                @NotNull(message = "티켓 개수는 필수입니다.")
                int ticketCount,

                @NotBlank(message = "티켓 시작일은 필수입니다.")
                String ticketStartDate,

                @NotBlank(message = "티켓 시작 시간은 필수입니다.")
                String ticketStartTime,

                @NotBlank(message = "티켓 종료일은 필수입니다.")
                String ticketEndDate,

                @NotBlank(message = "티켓 종료 시간은 필수입니다.")
                String ticketEndTime
        ) { }

        public record AdminEventImageInfo(
                String imageUrl
        ) { }
}
