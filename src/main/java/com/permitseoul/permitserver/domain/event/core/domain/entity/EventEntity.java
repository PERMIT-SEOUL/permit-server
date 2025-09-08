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
@Builder
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

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "line_up", columnDefinition = "TEXT")
    private String lineUp;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "min_age")
    private int minAge;

    @Column(name = "visible_end_date", nullable = false)
    private LocalDateTime visibleEndDate;

    @Column(name = "ticket_check_code", length = 30, nullable = false)
    private String ticketCheckCode;

    @Column(name = "visible_start_date")
    private LocalDateTime visibleStartDate;

    public static EventEntity create(final String name,
                                     final EventType eventType,
                                     final LocalDateTime startDate,
                                     final LocalDateTime endDate,
                                     final String venue,
                                     final String lineUp,
                                     final String details,
                                     final int minAge,
                                     final LocalDateTime visibleStartDate,
                                     final LocalDateTime visibleEndDate,
                                     final String ticketCheckCode) {
        return EventEntity.builder()
                .name(name)
                .eventType(eventType)
                .startDate(startDate)
                .endDate(endDate)
                .venue(venue)
                .lineUp(lineUp)
                .details(details)
                .minAge(minAge)
                .visibleStartDate(visibleStartDate)
                .visibleEndDate(visibleEndDate)
                .ticketCheckCode(ticketCheckCode)
                .build();
    }
}

