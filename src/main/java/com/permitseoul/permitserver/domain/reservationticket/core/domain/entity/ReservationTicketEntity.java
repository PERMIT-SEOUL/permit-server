package com.permitseoul.permitserver.domain.reservationticket.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_tickets")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class ReservationTicketEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_ticket_id", nullable = false)
    private Long reservationTicketId;

    @Column(name = "ticket_type_id", nullable = false)
    private long ticketTypeId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "count", nullable = false)
    private int count;

    public static ReservationTicketEntity create(final long ticketTypeId,
                                                 final String orderId,
                                                 final int count) {
        return ReservationTicketEntity.builder()
                .ticketTypeId(ticketTypeId)
                .orderId(orderId)
                .count(count)
                .build();
    }

}
