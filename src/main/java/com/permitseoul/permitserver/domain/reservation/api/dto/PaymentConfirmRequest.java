package com.permitseoul.permitserver.domain.reservation.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaymentConfirmRequest(

        @NotBlank(message = "orderId는  없으면 안됩니다.")
        String orderId,

        @NotBlank(message = "paymentKey는  없으면 안됩니다.")
        String paymentKey,

        @Positive(message = "totalAmount는 0보다 커야합니다.")
        int totalAmount
) { }
