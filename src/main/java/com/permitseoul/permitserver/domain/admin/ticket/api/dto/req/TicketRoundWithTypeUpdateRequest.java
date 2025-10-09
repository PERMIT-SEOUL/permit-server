package com.permitseoul.permitserver.domain.admin.ticket.api.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TicketRoundWithTypeUpdateRequest(
        @NotNull(message = "티켓라운드 ID는 필수입니다.")
        Long ticketRoundId,

        @NotBlank(message = "티켓라운드 이름은 필수입니다.")
        String ticketRoundName,

        @NotNull(message = "티켓라운드 판매 시작일은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketRoundSalesStartDate,

        @NotNull(message = "티켓라운드 판매 종료일은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketRoundSalesEndDate,

        @NotNull(message = "티켓타입 리스트는 필수입니다.")
        List<TicketTypeUpdateRequest> ticketTypes
) {
    public record TicketTypeUpdateRequest(

            //새로 추가된 타입일 경우 null
            Long id,

            @NotBlank(message = "티켓타입 이름은 필수입니다.")
            String name,

            @NotNull(message = "티켓타입 가격은 필수입니다.")
            @Positive(message = "티켓타입 가격은 0보다 커야 합니다.")
            BigDecimal price,

            @NotNull(message = "티켓타입 총 개수는 필수입니다.")
            @Positive(message = "티켓타입 총 개수는 0보다 커야 합니다.")
            Integer totalCount,

            @NotNull(message = "티켓타입 시작일은 필수입니다.")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime startDate,

            @NotNull(message = "티켓타입 종료일은 필수입니다.")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime endDate
    ) {}
}
