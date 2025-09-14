package com.permitseoul.permitserver.domain.reservation.core.domain;

import com.permitseoul.permitserver.domain.reservation.core.domain.entity.ReservationEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Reservation {
    private final long reservationId;
    private final long userId;
    private final long eventId;
    private final String orderId;
    private final BigDecimal totalAmount;
    private final String couponCode;
    private final ReservationStatus status;
    private final LocalDateTime tossPaymentResponseAt;

    public static Reservation fromEntity(final ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getReservationId(),
                reservationEntity.getUserId(),
                reservationEntity.getEventId(),
                reservationEntity.getOrderId(),
                reservationEntity.getTotalAmount(),
                reservationEntity.getCouponCode(),
                reservationEntity.getStatus(),
                reservationEntity.getTossPaymentResponseAt()
        );
    }
}
