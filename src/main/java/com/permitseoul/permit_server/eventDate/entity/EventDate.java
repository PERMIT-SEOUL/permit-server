package com.permitseoul.permit_server.eventDate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "event_dates")
public class EventDate {
    @Id
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
}

