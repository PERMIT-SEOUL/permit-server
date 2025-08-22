package com.permitseoul.permitserver.domain.reservation.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ReservationInfoRequest(

        @NotBlank(message = "eventId가 비어있습니다.")
        String eventId,

        @Nullable
        String couponCode,

        @Positive(message = "총 가격은 양수여야합니다.")
        BigDecimal totalAmount,

        @NotBlank(message = "orederId가 비어있습니다.")
        String orderId,

        @Valid
        @NotNull(message = "ticketTypeInfo가 비어있습니다.")
        List<TicketTypeInfo> ticketTypeInfos
) {
    public record TicketTypeInfo(

            @Positive(message = "id는 양수여야합니다.")
            long id,

            @Positive(message = "count는 양수여야합니다.")
            int count
    ) {
    }
}

