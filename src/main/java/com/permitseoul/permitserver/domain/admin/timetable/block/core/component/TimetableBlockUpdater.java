package com.permitseoul.permitserver.domain.admin.timetable.block.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimetableBlockUpdater {

    public void updateTimetableBlockTime(final TimetableBlockEntity timetableBlockEntity, final LocalDateTime startAt, final LocalDateTime endAt) {
        timetableBlockEntity.updateTime(startAt, endAt);
    }
}
