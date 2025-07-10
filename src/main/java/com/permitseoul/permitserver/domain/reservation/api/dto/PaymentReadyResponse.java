package com.permitseoul.permitserver.domain.reservation.api.dto;

public record PaymentReadyResponse(
        String orderName,
        String orderId,
        String userName,
        String userEmail,
        int totalAmount,
        String customerKey
) {
    public static PaymentReadyResponse of(final String orderName, final String orderId, final String userName, final String userEmail, final int totalAmount, final String customerKey) {
        return new PaymentReadyResponse(orderName, orderId, userName, userEmail, totalAmount, customerKey);
    }
}
