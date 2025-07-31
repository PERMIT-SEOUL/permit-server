package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto;

public record TimetableDetailResponse(
        String blockName,
        String blockCategory,
        String categoryColor,
        boolean isLiked,
        String information,
        String area,
        String imageUrl,
        String blockInfoUrl
) {
    public static TimetableDetailResponse of(final String blockName,
                                             final String blockCategory,
                                             final String categoryColor,
                                             final boolean isLiked,
                                             final String information,
                                             final String area,
                                             final String imageUrl,
                                             final String blockInfoRedirectUrl) {
        return new TimetableDetailResponse(
                blockName,
                blockCategory,
                categoryColor,
                isLiked,
                information,
                area,
                imageUrl,
                blockInfoRedirectUrl
        );
    }
}
