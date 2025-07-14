package com.permitseoul.permitserver.domain.reservation.api.dto;


public record PaymentCancelRequest(
        String cancelReason,
        String currency
) {
    public static PaymentCancelRequest of(final String cancelReason, final String currency) {
        return new PaymentCancelRequest(cancelReason, currency);
    }
}
