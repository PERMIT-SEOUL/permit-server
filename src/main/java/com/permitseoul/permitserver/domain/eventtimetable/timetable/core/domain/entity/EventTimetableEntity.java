package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_timetable")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventTimetableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_timetable_id", nullable = false)
    private Long eventTimetableId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    private EventTimetableEntity(long eventId, LocalDateTime startDate, LocalDateTime endDate) {
        this.eventId = eventId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static EventTimetableEntity create(final long eventId, final LocalDateTime startDate, final LocalDateTime endDate) {
        return new EventTimetableEntity(eventId, startDate, endDate);
    }
}
