package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record TimetableResponse(
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime endDate,
        List<Area> areas,
        List<Block> blocks
) {

    public static TimetableResponse of(final LocalDateTime startDate,
                                       final LocalDateTime endDate,
                                       final List<Area> areas,
                                       final List<Block> blocks) {
        return new TimetableResponse(startDate, endDate, areas, blocks);
    }

    public record Area(
            long areaId,
            String areaName,
            int sequence
    ) {
        public static Area of(final long areaId, final String areaName, final int sequence) {
            return new Area(areaId, areaName, sequence);
        }
    }

    public record Block(
            String blockId,
            String blockName,
            String blockColor,
            @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
            LocalDateTime blockStartDate,
            @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
            LocalDateTime blockEndDate,
            long blockAreaId,
            boolean isUserLiked
    ) {
        public static Block of(final String blockId,
                               final String blockName,
                               final String blockColor,
                               final LocalDateTime blockStartDate,
                               final LocalDateTime blockEndDate,
                               final long blockAreaId,
                               boolean isUserLiked) {
            return new Block(blockId, blockName, blockColor, blockStartDate, blockEndDate, blockAreaId, isUserLiked);
        }
    }
}
