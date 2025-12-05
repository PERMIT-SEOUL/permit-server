package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record TimetableResponse(
        String eventName,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime endDate,
        List<Stage> stages,
        List<Block> blocks
) {

    public static TimetableResponse of(final String eventName,
                                       final LocalDateTime startDate,
                                       final LocalDateTime endDate,
                                       final List<Stage> stages,
                                       final List<Block> blocks) {
        return new TimetableResponse(eventName, startDate, endDate, stages, blocks);
    }

    public record Stage(
            String stageNotionId,
            String stageName,
            int sequence
    ) {
        public static Stage of(final String stageNotionId, final String stageName, final int sequence) {
            return new Stage(stageNotionId, stageName, sequence);
        }
    }

    public record Block(
            String blockId,
            String blockName,
            String blockBackgroundColor,
            String blockLineColor,
            @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
            LocalDateTime blockStartDate,
            @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
            LocalDateTime blockEndDate,
            String blockStageNotionId,
            boolean isUserLiked
    ) {
        public static Block of(final String blockId,
                               final String blockName,
                               final String blockBackgroundColor,
                               final String blockLineColor,
                               final LocalDateTime blockStartDate,
                               final LocalDateTime blockEndDate,
                               final String blockStageNotionId,
                               boolean isUserLiked) {
            return new Block(blockId, blockName, blockBackgroundColor, blockLineColor, blockStartDate, blockEndDate, blockStageNotionId, isUserLiked);
        }
    }
}
