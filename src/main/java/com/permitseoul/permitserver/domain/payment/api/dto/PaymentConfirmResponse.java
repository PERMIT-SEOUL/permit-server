package com.permitseoul.permitserver.domain.payment.api.dto;

public record PaymentConfirmResponse(
        String eventName,
        String eventDate
) {
    public static PaymentConfirmResponse of(final String eventName, final String eventDate) {
        return new PaymentConfirmResponse(eventName, eventDate);
    }
}
