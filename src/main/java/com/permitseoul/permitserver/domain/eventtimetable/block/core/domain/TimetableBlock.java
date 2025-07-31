package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TimetableBlock {
    private final Long timetableBlockId;
    private final long timetableId;
    private final long timetableCategoryId;
    private final long timetableAreaId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String blockName;
    private final String artist;
    private final String imageUrl;
    private final String information;

    public static TimetableBlock fromEntity(final TimetableBlockEntity timetableBlockEntity) {
        return new TimetableBlock(
                timetableBlockEntity.getTimetableBlockId(),
                timetableBlockEntity.getTimetableId(),
                timetableBlockEntity.getTimetableCategoryId(),
                timetableBlockEntity.getTimetableAreaId(),
                timetableBlockEntity.getStartDate(),
                timetableBlockEntity.getEndDate(),
                timetableBlockEntity.getBlockName(),
                timetableBlockEntity.getArtist(),
                timetableBlockEntity.getImageUrl(),
                timetableBlockEntity.getInformation()
        );
    }
}
