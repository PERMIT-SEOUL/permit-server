package com.permitseoul.permit_server.event.domain.entity;

import com.permitseoul.permit_server.event.domain.EventType;
import com.permitseoul.permit_server.global.domain.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "line_up", columnDefinition = "TEXT")
    private String lineUp;

    @Column(name = "introduction", columnDefinition = "TEXT") ///255보다 더 길어질 수 있기 때문
    private String introduction;

    @Column(name = "min_age", nullable = false)
    private int minAge;

    @Column(name = "ticket_count")
    private int ticketCount;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "remain_ticket_count")
    private int remainTicketCount;

    @Column(name = "ticket_check_code", length = 10)
    private String ticketCheckCode;

}

