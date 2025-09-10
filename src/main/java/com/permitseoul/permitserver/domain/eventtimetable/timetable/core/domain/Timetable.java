package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Timetable {
    private final Long timetableId;
    private final long eventId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public static Timetable fromEntity(final TimetableEntity timetableEntity) {
        return new Timetable(
                timetableEntity.getTimetableId(),
                timetableEntity.getEventId(),
                timetableEntity.getStartAt(),
                timetableEntity.getEndAt()
        );
    }
}
