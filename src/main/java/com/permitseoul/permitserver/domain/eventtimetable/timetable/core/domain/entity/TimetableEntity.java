package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_timetable")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id", nullable = false)
    private Long timetableId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "notion_timetable_datasource_id", nullable = false, length = 100)
    private String notionTimetableDatasourceId;

    @Column(name = "notion_stage_datasource_id", nullable = false, length = 100)
    private String notionStageDatasourceId;

    @Column(name = "notion_category_datasource_id", nullable = false, length = 100)
    private String notionCategoryDatasourceId;

    private TimetableEntity(
            long eventId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String notionTimetableDatasourceId,
            String notionStageDatasourceId,
            String notionCategoryDatasourceId
    ) {
        this.eventId = eventId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.notionTimetableDatasourceId = notionTimetableDatasourceId;
        this.notionStageDatasourceId = notionStageDatasourceId;
        this.notionCategoryDatasourceId = notionCategoryDatasourceId;
    }

    public static TimetableEntity create(
            final long eventId,
            final LocalDateTime startAt,
            final LocalDateTime endAt,
            final String notionTimetableDatasourceId,
            final String notionStageDatasourceId,
            final String notionCategoryDatasourceId
    ) {
        return new TimetableEntity(
                eventId,
                startAt,
                endAt,
                notionTimetableDatasourceId,
                notionStageDatasourceId,
                notionCategoryDatasourceId
        );
    }

    public void update(final LocalDateTime timetableStartAtReq,
                       final LocalDateTime timetableEndAtReq,
                       final String notionTimetableDataSourceIdReq,
                       final String notionCategoryDataSourceIdReq,
                       final String notionStageDataSourceIdReq) {
        this.startAt = timetableStartAtReq;
        this.endAt = timetableEndAtReq;
        this.notionTimetableDatasourceId = notionTimetableDataSourceIdReq;
        this.notionCategoryDatasourceId = notionCategoryDataSourceIdReq;
        this.notionStageDatasourceId = notionStageDataSourceIdReq;
    }
}
