package com.permitseoul.permitserver.domain.reservationsession.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "reservation_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSessionEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_sessions_id", nullable = false)
    private Long reservationSessionsId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "session_key", nullable = false)
    private String sessionKey;

    @Column(name = "successful", nullable = false)
    private boolean successful;

    private ReservationSessionEntity(long userId, String orderId, String sessionKey) {
        this.userId = userId;
        this.orderId = orderId;
        this.sessionKey = sessionKey;
        this.successful = false;
    }

    public static ReservationSessionEntity create(long userId, String orderId, String sessionKey) {
        return new ReservationSessionEntity(userId, orderId, sessionKey);
    }
}
