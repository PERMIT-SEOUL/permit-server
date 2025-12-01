package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record TimetableDetailResponse(
        String blockName,
        String blockCategory,
        String categoryBackgroundColor,
        String categoryLineColor,
        boolean isLiked,
        String information,
        String stage,
        String blockInfoUrl,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime endDate,
        List<MediaInfo> media

) {
    public static TimetableDetailResponse of(final String blockName,
                                             final String blockCategory,
                                             final String categoryBackgroundColor,
                                             final String categoryLineColor,
                                             final boolean isLiked,
                                             final String information,
                                             final String stage,
                                             final String blockInfoRedirectUrl,
                                             final LocalDateTime startDate,
                                             final LocalDateTime endDate,
                                             final List<MediaInfo> mediaInfo) {
        return new TimetableDetailResponse(
                blockName,
                blockCategory,
                categoryBackgroundColor,
                categoryLineColor,
                isLiked,
                information,
                stage,
                blockInfoRedirectUrl,
                startDate,
                endDate,
                mediaInfo
        );
    }

    public record MediaInfo(
            String mediaUrl
    ) {
        public static MediaInfo of(final String mediaUrl) {
            return new MediaInfo(mediaUrl);
        }
    }
}
