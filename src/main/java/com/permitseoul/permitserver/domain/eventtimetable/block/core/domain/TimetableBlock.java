package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TimetableBlock {
    private final Long timetableBlockId;
    private final long timetableId;
    private final String timetableCategoryNotionId;
    private final String timetableStageNotionId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String blockName;
    private final String artist;
    private final String information;
    private final String blockInfoRedirectUrl;

    public static TimetableBlock fromEntity(final TimetableBlockEntity timetableBlockEntity) {
        return new TimetableBlock(
                timetableBlockEntity.getTimetableBlockId(),
                timetableBlockEntity.getTimetableId(),
                timetableBlockEntity.getTimetableCategoryNotionId(),
                timetableBlockEntity.getTimetableStageNotionId(),
                timetableBlockEntity.getStartAt(),
                timetableBlockEntity.getEndAt(),
                timetableBlockEntity.getBlockName(),
                timetableBlockEntity.getArtist(),
                timetableBlockEntity.getInformation(),
                timetableBlockEntity.getBlockInfoRedirectUrl()
        );
    }
}
