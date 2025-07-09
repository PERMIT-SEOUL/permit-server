package com.permitseoul.permitserver.domain.reservation.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PaymentReadyRequest(

        @Positive(message = "eventId는 양수여야 합니다.")
        long eventId,

        @Nullable
        String couponCode,

        @Positive(message = "총 가격은 양수여야합니다.")
        int totalAmount,

        @NotBlank
        String orderId,

        @Valid
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

