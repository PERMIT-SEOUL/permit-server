package com.permitseoul.permitserver.domain.payment.api.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        String currency,
        BigDecimal totalAmount,
        String requestedAt,
        String approvedAt
) {
}
