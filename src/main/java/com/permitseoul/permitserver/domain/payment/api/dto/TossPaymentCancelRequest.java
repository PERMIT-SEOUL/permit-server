package com.permitseoul.permitserver.domain.payment.api.dto;


import com.permitseoul.permitserver.domain.payment.core.domain.Currency;

public record TossPaymentCancelRequest(
        String cancelReason,
        Currency currency
) {
    public static TossPaymentCancelRequest of(final String cancelReason, final Currency currency) {
        return new TossPaymentCancelRequest(cancelReason, currency);
    }
}
