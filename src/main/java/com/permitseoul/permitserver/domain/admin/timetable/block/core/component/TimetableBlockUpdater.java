package com.permitseoul.permitserver.domain.admin.timetable.block.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimetableBlockUpdater {

    public void updateTimetableBlockTime(final TimetableBlockEntity timetableBlockEntity, final LocalDateTime startAt, final LocalDateTime endAt) {
        timetableBlockEntity.updateTime(startAt, endAt);
    }

    public void updateTimetableBlockArtistAndBlockName(final TimetableBlockEntity timetableBlockEntity, final String artist) {
        timetableBlockEntity.updateArtistAndBlockName(artist);
    }

    public void updateTimetableBlockCategoryRelationRowId(final TimetableBlockEntity timetableBlockEntity, final String newCategoryRelationRowId) {
        timetableBlockEntity.updateCategoryRelationRowId(newCategoryRelationRowId);
    }

    public void updateTimetableBlockStageRelationRowId(final TimetableBlockEntity timetableBlockEntity, final String newStageRelationRowId) {
        timetableBlockEntity.updateStageRelationRowId(newStageRelationRowId);
    }

    public void updateTimetableBlockDetails(final TimetableBlockEntity timetableBlockEntity, final String newDetails) {
        timetableBlockEntity.updateDetails(newDetails);
    }
}
