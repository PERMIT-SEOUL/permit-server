package com.permitseoul.permitserver.domain.eventdate.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "event_dates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_dates_id", nullable = false)
    private Long eventDatesId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private EventDateEntity(long eventId, LocalDate eventDate, LocalTime startTime, LocalTime endTime) {
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

