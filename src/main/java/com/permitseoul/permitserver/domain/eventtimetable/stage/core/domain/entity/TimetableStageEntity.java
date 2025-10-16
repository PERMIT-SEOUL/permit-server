package com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_stages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimetableStageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_stage_id", nullable = false)
    private Long timetableStageId;

    @Column(name = "event_timetable_id", nullable = false)
    private long timetableId;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "notion_stage_row_id", nullable = false)
    private String notionStageRowId;

    private TimetableStageEntity(long timetableId, String stageName, int sequence, String notionStageRowId) {
        this.timetableId = timetableId;
        this.stageName = stageName;
        this.sequence = sequence;
        this.notionStageRowId = notionStageRowId;
    }

    public static TimetableStageEntity create(final long eventTimetableId, final String stageName, final int sequence, final String notionStageRowId) {
        return new TimetableStageEntity(eventTimetableId, stageName, sequence, notionStageRowId);
    }
}
