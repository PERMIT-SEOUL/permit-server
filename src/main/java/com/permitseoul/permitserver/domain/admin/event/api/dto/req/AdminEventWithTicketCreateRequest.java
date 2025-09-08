package com.permitseoul.permitserver.domain.admin.event.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminEventWithTicketCreateRequest(

        @NotNull(message = "행사 노출 시작일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate eventExposureStartDate,

        @NotNull(message = "행사 노출 시작 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime eventExposureStartTime,

        @NotNull(message = "행사 노출 종료일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate eventExposureEndDate,

        @NotNull(message = "행사 노출 종료 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime eventExposureEndTime,

        @NotBlank(message = "검증 코드는 필수입니다.")
        @Size(max = 30, message = "검증 코드는 30자를 초과할 수 없습니다.")
        String verificationCode,

        @NotBlank(message = "행사명은 필수입니다.")
        String name,

        @NotNull(message = "행사 타입은 필수입니다.")
        EventType eventType,

        @NotNull(message = "행사 시작일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @NotNull(message = "행사 시작 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "행사 종료일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @NotNull(message = "행사 종료 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime endTime,

        @NotBlank(message = "행사 장소는 필수입니다.")
        String venue,

        String lineup,
        String details,

        @NotEmpty(message = "행사 이미지는 최소 1개 이상이어야 합니다.")
        List<AdminEventImageInfo> images,

        @Min(value = 0, message = "최소 나이는 0 이상이어야 합니다.")
        int minAge,

        @NotBlank(message = "티켓 차수 이름은 필수입니다.")
        String ticketRoundName,

        @NotNull(message = "티켓 차수 시작일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate roundSalesStartDate,

        @NotNull(message = "티켓 차수 시작 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime roundSalesStartTime,

        @NotNull(message = "티켓 차수 종료일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate roundSalesEndDate,

        @NotNull(message = "티켓 차수 종료 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime roundSalesEndTime,

        @NotEmpty(message = "티켓 정보는 최소 1개 이상이어야 합니다.")
        @Valid
        List<TicketTypeRequest> ticketTypes
) {
        public record TicketTypeRequest(
                @NotBlank(message = "티켓 이름은 필수입니다.")
                String ticketName,

                @NotNull(message = "가격은 필수입니다.")
                BigDecimal price,

                @Min(value = 1, message = "티켓 개수는 1 이상이어야 합니다.")
                int ticketCount,

                @NotNull(message = "티켓 시작일은 필수입니다.")
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                LocalDate ticketStartDate,

                @NotNull(message = "티켓 시작 시간은 필수입니다.")
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
                LocalTime ticketStartTime,

                @NotNull(message = "티켓 종료일은 필수입니다.")
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                LocalDate ticketEndDate,

                @NotNull(message = "티켓 종료 시간은 필수입니다.")
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
                LocalTime ticketEndTime
        ) { }

        public record AdminEventImageInfo(
                String imageUrl
        ) { }
}
