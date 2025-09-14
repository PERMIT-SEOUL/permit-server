package com.permitseoul.permitserver.domain.payment.api.dto;

import java.math.BigDecimal;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static TossPaymentRequest of(final String paymentKey, final String orderId, final BigDecimal amount) {
        return new TossPaymentRequest(paymentKey, orderId, amount);
    }
}
