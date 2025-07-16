package com.permitseoul.permitserver.domain.reservation.api.dto;

import java.math.BigDecimal;

public record ReservationInfoResponse(
        String orderName,
        String orderId,
        String userName,
        String userEmail,
        BigDecimal totalAmount,
        String customerKey
) {
    public static ReservationInfoResponse of(final String orderName, final String orderId, final String userName, final String userEmail, final BigDecimal totalAmount, final String customerKey) {
        return new ReservationInfoResponse(orderName, orderId, userName, userEmail, totalAmount, customerKey);
    }
}
