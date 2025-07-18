package com.permitseoul.permitserver.domain.reservation.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.global.exception.IllegalEnumTransitionException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Column(name = "toss_payment_receive_time")
    private LocalDateTime tossPaymentReceiveTime;

    private ReservationEntity(long userId, long eventId, String orderId, BigDecimal totalAmount, String couponCode) {
        this.userId = userId;
        this.eventId = eventId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.couponCode = couponCode;
        this.status = ReservationStatus.RESERVED;
        this.tossPaymentReceiveTime = null;
    }

    public static ReservationEntity create(final long userId,
                                           final long eventId,
                                           final String orderId,
                                           final BigDecimal totalAmount,
                                           final String couponCode
                                          ) {
        return new ReservationEntity(userId, eventId, orderId, totalAmount, couponCode);
    }

    public void updateReservationStatus(final ReservationStatus status) {
        if(!this.status.canTransitionTo(status)) {
            throw new IllegalEnumTransitionException();
        }
        this.status = status;
    }

    public void updateTossPaymentReceiveTime(final LocalDateTime tossPaymentReceiveTime) {
        this.tossPaymentReceiveTime = tossPaymentReceiveTime;
    }
}

