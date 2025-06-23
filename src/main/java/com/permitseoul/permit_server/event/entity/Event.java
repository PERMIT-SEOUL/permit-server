package com.permitseoul.permit_server.event.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
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

    @Column(name = "line_up")
    private String lineUp;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "min_age", nullable = false)
    private int minAge;

    @Column(name = "ticket_count")
    private int ticketCount;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "remain_ticket_count")
    private int remainTicketCount;

    @Column(name = "ticket_check_code")
    private String ticketCheckCode;

}

