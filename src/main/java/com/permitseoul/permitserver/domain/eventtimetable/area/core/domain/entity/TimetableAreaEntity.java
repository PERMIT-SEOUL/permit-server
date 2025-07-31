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
public class TimetableAreaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_area_id", nullable = false)
    private Long timetableAreaId;

    @Column(name = "event_timetable_id", nullable = false)
    private long timetableId;

    @Column(name = "area_name", nullable = false)
    private String areaName;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    private TimetableAreaEntity(long timetableId, String areaName, int sequence) {
        this.timetableId = timetableId;
        this.areaName = areaName;
        this.sequence = sequence;
    }

    public static TimetableAreaEntity create(final long eventTimetableId, final String areaName, final int sequence) {
        return new TimetableAreaEntity(eventTimetableId, areaName, sequence);
    }
}