package com.permitseoul.permitserver.domain.payment.api.dto;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        String currency,
        int totalAmount,
        String requestedAt,
        String approvedAt
) {
}
