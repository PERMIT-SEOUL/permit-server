package com.permitseoul.permitserver.domain.tickettype.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_type")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class TicketTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    @Column(name = "ticket_round_id", nullable = false)
    private long ticketRoundId;

    @Column(name = "ticket_type_name")
    private String ticketTypeName;

    @Column(name = "ticket_price", nullable = false)
    private int ticketPrice;

    @Column(name = "ticket_dates", nullable = false)
    private LocalDateTime ticketDates;

}
