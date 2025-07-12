package com.permitseoul.permitserver.domain.payment.api.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static PaymentRequest of(final String paymentKey, final String orderId, final BigDecimal amount) {
        return new PaymentRequest(paymentKey, orderId, amount);
    }
}
