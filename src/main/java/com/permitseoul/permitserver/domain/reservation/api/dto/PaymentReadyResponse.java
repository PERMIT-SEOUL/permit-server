package com.permitseoul.permitserver.domain.reservation.api.dto;

public record PaymentReadyResponse(
        String orderName,
        String orderId,
        String userEmail,
        String userName,
        int totalAmount,
        String customerKey
) {
    public static PaymentReadyResponse of(final String orderName, final String orderId, final String userEmail, final String userName, final int totalAmount, final String customerKey) {
        return new PaymentReadyResponse(orderName, orderId, userEmail, userName, totalAmount, customerKey);
    }
}
