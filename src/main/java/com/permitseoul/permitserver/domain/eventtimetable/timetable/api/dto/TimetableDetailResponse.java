package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TimetableDetailResponse(
        String blockName,
        String blockCategory,
        String categoryColor,
        boolean isLiked,
        String information,
        String area,
        String imageUrl,
        String blockInfoUrl,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime endDate
) {
    public static TimetableDetailResponse of(final String blockName,
                                             final String blockCategory,
                                             final String categoryColor,
                                             final boolean isLiked,
                                             final String information,
                                             final String area,
                                             final String imageUrl,
                                             final String blockInfoRedirectUrl,
                                             final LocalDateTime startDate,
                                             final LocalDateTime endDate) {
        return new TimetableDetailResponse(
                blockName,
                blockCategory,
                categoryColor,
                isLiked,
                information,
                area,
                imageUrl,
                blockInfoRedirectUrl,
                startDate,
                endDate
        );
    }
}
