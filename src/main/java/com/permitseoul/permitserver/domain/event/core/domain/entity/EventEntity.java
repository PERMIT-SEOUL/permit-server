package com.permitseoul.permitserver.domain.event.core.domain.entity;

import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
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

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "line_up", columnDefinition = "TEXT")
    private String lineUp;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "min_age")
    private int minAge;

    @Column(name = "visible_end_at", nullable = false)
    private LocalDateTime visibleEndAt;

    @Column(name = "ticket_check_code", length = 30, nullable = false)
    private String ticketCheckCode;

    @Column(name = "visible_start_at")
    private LocalDateTime visibleStartAt;

    public static EventEntity create(final String name,
                                     final EventType eventType,
                                     final LocalDateTime startDate,
                                     final LocalDateTime endDate,
                                     final String venue,
                                     final String lineUp,
                                     final String details,
                                     final int minAge,
                                     final LocalDateTime visibleStartAt,
                                     final LocalDateTime visibleEndAt,
                                     final String ticketCheckCode) {
        return EventEntity.builder()
                .name(name)
                .eventType(eventType)
                .startAt(startDate)
                .endAt(endDate)
                .venue(venue)
                .lineUp(lineUp)
                .details(details)
                .minAge(minAge)
                .visibleStartAt(visibleStartAt)
                .visibleEndAt(visibleEndAt)
                .ticketCheckCode(ticketCheckCode)
                .build();
    }
}

