package com.permitseoul.permitserver.domain.eventtimetable.area.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.entity.TimetableAreaEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TimetableArea {
    private final long timetableAreaId;
    private final long timetableId;
    private final String areaName;
    private final int sequence;

    public static TimetableArea fromEntity(final TimetableAreaEntity timetableAreaEntity) {
        return new TimetableArea(
                timetableAreaEntity.getTimetableAreaId(),
                timetableAreaEntity.getTimetableId(),
                timetableAreaEntity.getAreaName(),
                timetableAreaEntity.getSequence()
        );
    }
}
