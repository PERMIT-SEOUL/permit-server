package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity;

import com.permitseoul.permitserver.global.exception.LocalDateTimeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_timetable_block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimetableBlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_block_id", nullable = false)
    private Long timetableBlockId;

    @Column(name = "timetable_id", nullable = false)
    private long timetableId;

    @Column(name = "notion_timetable_category_relation_row_id", nullable = false)
    private String notionTimetableCategoryRelationRowId;

    @Column(name = "notion_timetable_stage_relation_row_id", nullable = false)
    private String notionTimetableStageRelationRowId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "block_name", nullable = false)
    private String blockName;

    @Column(name = "artist")
    private String artist;

    @Column(name = "information", nullable = false)
    private String information;

    @Column(name = "block_info_redirect_url")
    private String blockInfoRedirectUrl;

    @Column(name = "notion_timetable_block_row_id", nullable = false)
    private String notionTimetableBlockRowId;


    private TimetableBlockEntity(
            long timetableId,
            String notionTimetableCategoryRelationRowId,
            String notionTimetableStageRelationRowId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String blockName,
            String artist,
            String information,
            String blockInfoRedirectUrl,
            String notionTimetableBlockRowId
    ) {
        validateDateTime(startAt, endAt);

        this.timetableId = timetableId;
        this.notionTimetableCategoryRelationRowId = notionTimetableCategoryRelationRowId;
        this.notionTimetableStageRelationRowId = notionTimetableStageRelationRowId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.blockName = blockName;
        this.artist = artist;
        this.information = information;
        this.blockInfoRedirectUrl = blockInfoRedirectUrl;
        this.notionTimetableBlockRowId = notionTimetableBlockRowId;
    }

    public static TimetableBlockEntity create(final long timetableId,
                                              final String notionTimetableCategoryRelationRowId,
                                              final String notionTimetableStageRelationRowId,
                                              final LocalDateTime startAt,
                                              final LocalDateTime endAt,
                                              final String blockName,
                                              final String artist,
                                              final String information,
                                              final String blockInfoRedirectUrl,
                                              final String notionTimetableBlockRowId) {
        return new TimetableBlockEntity(timetableId, notionTimetableCategoryRelationRowId, notionTimetableStageRelationRowId, startAt, endAt, blockName, artist, information, blockInfoRedirectUrl, notionTimetableBlockRowId);
    }

    private void validateDateTime(final LocalDateTime startAt, final LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new LocalDateTimeException();
        }
    }

    public void updateTime(final LocalDateTime startAt, final LocalDateTime endAt) {
        validateDateTime(startAt, endAt);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updateArtistAndBlockName(final String artistWithBlockName) {
        this.artist = artistWithBlockName;
        this.blockName = artistWithBlockName;
    }

    public void updateCategoryRelationRowId(final String newCategoryRelationRowId) {
        this.notionTimetableCategoryRelationRowId = newCategoryRelationRowId;
    }

    public void updateStageRelationRowId(final String newStageRelationRowId) {
        this.notionTimetableStageRelationRowId = newStageRelationRowId;
    }

    public void updateDetails(final String newDetails) {
        this.information = newDetails;
    }

    public void updateRedirectUrl(final String newRedirectUrl) {
        this.blockInfoRedirectUrl = newRedirectUrl;
    }
}