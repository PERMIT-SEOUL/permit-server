package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_timetable")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id", nullable = false)
    private Long timetableId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    private TimetableEntity(long eventId, LocalDateTime startAt, LocalDateTime endAt) {
        this.eventId = eventId;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static TimetableEntity create(final long eventId, final LocalDateTime startAt, final LocalDateTime endAt) {
        return new TimetableEntity(eventId, startAt, endAt);
    }
}
