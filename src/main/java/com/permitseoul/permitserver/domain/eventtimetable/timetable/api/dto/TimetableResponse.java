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
    public record Area(
            long areaId,
            String areaName,
            int sequence
    ) { }

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
    ) { }
}
