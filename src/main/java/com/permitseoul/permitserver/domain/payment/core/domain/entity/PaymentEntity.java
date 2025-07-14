package com.permitseoul.permitserver.domain.payment.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.payment.core.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payments_id", nullable = false)
    private Long paymentId;

    @Column(name = "reservation_id", nullable = false)
    private long reservationId;

    @Column(name = "order_id", nullable = false, length = 64, unique = true)
    private String orderId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "payment_key", nullable = false, length = 200, unique = true)
    private String paymentKey;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "currency", nullable = false)
    private String currency;

    public PaymentEntity(
                         long reservationId,
                         String orderId,
                         long eventId,
                         String paymentKey,
                         BigDecimal totalAmount,
                         String currency
                      ) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.eventId = eventId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.status = PaymentStatus.SUCCESS;
    }
}

