package com.permitseoul.permit_server.payment.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.permitseoul.permit_server.global.domain.BaseTimeEntity;
import com.permitseoul.permit_server.reservation.entity.Reservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment  extends BaseTimeEntity {
    @Id
    @Column(name = "payments_id", nullable = false)
    private Long paymentsId;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "payment_key", nullable = false)
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

