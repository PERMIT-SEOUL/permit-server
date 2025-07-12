package com.permitseoul.permitserver.domain.reservation.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reservations")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class ReservationEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "coupon_code")
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "pay_error_message")
    private String payErrorMessage;

    public static ReservationEntity create(final long userId,
                                           final long eventId,
                                           final String orderId,
                                           final BigDecimal totalAmount,
                                           final String couponCode,
                                           final ReservationStatus status,
                                           final String payErrorMessage) {
        return ReservationEntity.builder()
                .userId(userId)
                .eventId(eventId)
                .orderId(orderId)
                .totalAmount(totalAmount)
                .couponCode(couponCode)
                .payErrorMessage(payErrorMessage)
                .status(status).build();
    }
}

