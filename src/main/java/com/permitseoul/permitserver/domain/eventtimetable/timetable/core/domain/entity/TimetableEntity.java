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

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    private TimetableEntity(long eventId, LocalDateTime startDate, LocalDateTime endDate) {
        this.eventId = eventId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TimetableEntity create(final long eventId, final LocalDateTime startDate, final LocalDateTime endDate) {
        return new TimetableEntity(eventId, startDate, endDate);
    }
}
