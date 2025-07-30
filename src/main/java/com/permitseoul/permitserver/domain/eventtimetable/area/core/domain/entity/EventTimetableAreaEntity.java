package com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_area")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventTimetableAreaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_timetable_area_id", nullable = false)
    private Long eventTimetableAreaId;

    @Column(name = "event_timetable_id", nullable = false)
    private long eventTimetableId;

    @Column(name = "area_name", nullable = false)
    private String areaName;

    private EventTimetableAreaEntity(long eventTimetableId, String areaName) {
        this.eventTimetableId = eventTimetableId;
        this.areaName = areaName;
    }

    public static EventTimetableAreaEntity create(final long eventTimetableId, final String areaName) {
        return new EventTimetableAreaEntity(eventTimetableId, areaName);
    }
}