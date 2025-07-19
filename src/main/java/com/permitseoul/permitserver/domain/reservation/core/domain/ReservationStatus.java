package com.permitseoul.permitserver.domain.reservation.core.domain;

public enum ReservationStatus {
    RESERVED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    TICKET_ISSUED,
    PAYMENT_CANCELED,
    ;

    public boolean canTransitionTo(final ReservationStatus to) {
        return switch (this) {
            case RESERVED -> to == PAYMENT_SUCCESS || to == PAYMENT_FAILED || to == TICKET_ISSUED;
            case PAYMENT_SUCCESS -> to == TICKET_ISSUED;
            case TICKET_ISSUED -> to == PAYMENT_CANCELED;
            case PAYMENT_FAILED, PAYMENT_CANCELED -> false;
        };
    }
}
