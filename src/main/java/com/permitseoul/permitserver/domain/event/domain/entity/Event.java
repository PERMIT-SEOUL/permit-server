package com.permitseoul.permitserver.domain.event.domain.entity;

import com.permitseoul.permitserver.domain.event.domain.EventType;
import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
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

    @Column(name = "venue")
    private String venue;

    @Column(name = "line_up", columnDefinition = "TEXT")
    private String lineUp;

    @Column(name = "introduction", columnDefinition = "TEXT") ///255보다 더 길어질 수 있기 때문
    private String introduction;

    @Column(name = "min_age", nullable = false)
    private Integer minAge;

    @Column(name = "ticket_count")
    private int ticketCount;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "remain_ticket_count")
    private int remainTicketCount;

    @Column(name = "ticket_check_code", length = 10)
    private String ticketCheckCode;

}

