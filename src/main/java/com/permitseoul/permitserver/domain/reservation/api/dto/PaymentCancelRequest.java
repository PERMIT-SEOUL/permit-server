package com.permitseoul.permitserver.domain.reservation.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCancelRequest(
        @NotBlank(message = "orderId가 비어있습니다.") String orderId
) {
}
