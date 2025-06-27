package com.permitseoul.permitserver.payment.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.payment.domain.PaymentStatus;
import com.permitseoul.permitserver.payment.domain.PaymentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment  extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payments_id", nullable = false)
    private Long paymentId;

    @JoinColumn(name = "reservation_id", nullable = false)
    private long reservationId;

    @Column(name = "order_id", nullable = false, length = 64, unique = true)
    private String orderId;

    @Column(name = "payment_key", nullable = false, length = 200, unique = true)
    private String paymentKey;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

}

