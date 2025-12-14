package com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.res;

public record TimetableInfoResponse(
        String eventId,
        long timetableId,
        String timetableStartDate,  // (2025-11-03)
        String timetableStartTime,  //(15:30)
        String timetableEndDate,    // (2025-11-03)
        String timetableEndTime,    //(15:30)
        String notionTimetableDataSourceId,
        String notionCategoryDataSourceId,
        String notionStageDataSourceId
) {
    public static TimetableInfoResponse of(final String eventId,
            final long timetableId,
                                           final String timetableStartDate,
                                           final String timetableStartTime,
                                           final String timetableEndDate,
                                           final String timetableEndTime,
                                           final String notionTimetableDataSourceId,
                                           final String notionCategoryDataSourceId,
                                           final String notionStageDataSourceId) {
        return new TimetableInfoResponse(
                eventId,
                timetableId,
                timetableStartDate,
                timetableStartTime,
                timetableEndDate,
                timetableEndTime,
                notionTimetableDataSourceId,
                notionCategoryDataSourceId,
                notionStageDataSourceId
        );
    }
}
