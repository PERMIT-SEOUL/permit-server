package com.permitseoul.permitserver.domain.event.core.domain.entity;

import com.permitseoul.permitserver.domain.event.core.domain.EventType;
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
public class EventEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; //2025.11.20 15:40

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; //2025.11.20 15:40

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "line_up", columnDefinition = "TEXT")
    private String lineUp;

    @Column(name = "details", columnDefinition = "TEXT") ///255보다 더 길어질 수 있기 때문
    private String details;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "ticket_count", nullable = false)
    private int ticketCount;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "remain_ticket_count", nullable = false)
    private int remainTicketCount;

    @Column(name = "ticket_check_code", length = 10, nullable = false)
    private String ticketCheckCode;

    @Column(name = "visible_start_date")
    private LocalDateTime visibleStartDate;
}

