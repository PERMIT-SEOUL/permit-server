package com.permitseoul.permitserver.domain.payment.core.domain;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Payment {
    private final Long paymentId;
    private final long reservationId;
    private final String orderId;
    private final long eventId;
    private final String paymentKey;
    private final int totalAmount;
    private final PaymentStatus status;
    private final LocalDateTime canceledAt;
    private final String currency;
    private final String canceledErrorMessage;

    public static Payment fromEntity(final PaymentEntity paymentEntity) {
        return new Payment(
                paymentEntity.getPaymentId(),
                paymentEntity.getReservationId(),
                paymentEntity.getOrderId(),
                paymentEntity.getEventId(),
                paymentEntity.getPaymentKey(),
                paymentEntity.getTotalAmount(),
                paymentEntity.getStatus(),
                paymentEntity.getCanceledAt(),
                paymentEntity.getCurrency(),
                paymentEntity.getCanceledErrorMessage()
        );
    }
}
