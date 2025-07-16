package com.permitseoul.permitserver.domain.payment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentConfirmRequest(

        @NotBlank(message = "orderId는  없으면 안됩니다.")
        String orderId,

        @NotBlank(message = "paymentKey는  없으면 안됩니다.")
        String paymentKey,

        @Positive(message = "totalAmount는 0보다 커야합니다.")
        BigDecimal totalAmount
) { }
