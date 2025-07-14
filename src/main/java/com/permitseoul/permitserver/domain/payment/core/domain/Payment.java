package com.permitseoul.permitserver.domain.payment.core.domain;

import com.permitseoul.permitserver.domain.payment.core.domain.entity.PaymentEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class Payment {
    private final Long paymentId;
    private final long reservationId;
    private final String orderId;
    private final long eventId;
    private final String paymentKey;
    private final BigDecimal totalAmount;
    private final PaymentStatus status;
    private final Currency currency;
    private final String requestAt;
    private final String approveAt;


    public static Payment fromEntity(final PaymentEntity paymentEntity) {
        return new Payment(
                paymentEntity.getPaymentId(),
                paymentEntity.getReservationId(),
                paymentEntity.getOrderId(),
                paymentEntity.getEventId(),
                paymentEntity.getPaymentKey(),
                paymentEntity.getTotalAmount(),
                paymentEntity.getStatus(),
                paymentEntity.getCurrency(),
                paymentEntity.getRequestedAt(),
                paymentEntity.getApprovedAt()
        );
    }
}
