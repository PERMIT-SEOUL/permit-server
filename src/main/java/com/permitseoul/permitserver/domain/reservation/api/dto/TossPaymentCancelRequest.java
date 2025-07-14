package com.permitseoul.permitserver.domain.reservation.api.dto;


public record TossPaymentCancelRequest(
        String cancelReason,
        String currency
) {
    public static TossPaymentCancelRequest of(final String cancelReason, final String currency) {
        return new TossPaymentCancelRequest(cancelReason, currency);
    }
}
