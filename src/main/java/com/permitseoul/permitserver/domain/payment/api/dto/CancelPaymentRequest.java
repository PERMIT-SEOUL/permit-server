package com.permitseoul.permitserver.domain.payment.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentRequest(
        @NotBlank(message = "orderId는 비어있으면 안됩니다.")
        String orderId
) {
}
