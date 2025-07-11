package com.permitseoul.permitserver.domain.payment.api.dto;

public record PaymentRequest(
        String paymentKey,
        String orderId,
        int amount
) {
    public static PaymentRequest of(final String paymentKey, final String orderId, final int amount) {
        return new PaymentRequest(paymentKey, orderId, amount);
    }
}
