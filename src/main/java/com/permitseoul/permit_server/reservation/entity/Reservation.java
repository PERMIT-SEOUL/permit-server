package com.permitseoul.permit_server.reservation.entity;

import com.permitseoul.permit_server.global.domain.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation extends BaseTimeEntity {
    @Id
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "is_coupon_used", nullable = false)
    private boolean isCouponUsed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
}

