package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimeTableIllegalArgumentException;
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

    @Column(name = "timetable_category_id", nullable = false)
    private long timetableCategoryId;

    @Column(name = "timetable_area_id", nullable = false)
    private long timetableAreaId;

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


    private TimetableBlockEntity(
            long timetableId,
            long timetableCategoryId,
            long timetableAreaId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String blockName,
            String artist,
            String information,
            String blockInfoRedirectUrl
    ) {
        validateDateTime(startAt, endAt);

        this.timetableId = timetableId;
        this.timetableCategoryId = timetableCategoryId;
        this.timetableAreaId = timetableAreaId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.blockName = blockName;
        this.artist = artist;
        this.information = information;
        this.blockInfoRedirectUrl = blockInfoRedirectUrl;
    }

    public static TimetableBlockEntity create(final long timetableId,
                                              final long timetableCategoryId,
                                              final long timetableAreaId,
                                              final LocalDateTime startAt,
                                              final LocalDateTime endAt,
                                              final String blockName,
                                              final String artist,
                                              final String information,
                                              final String blockInfoRedirectUrl) {
        return new TimetableBlockEntity(timetableId, timetableCategoryId, timetableAreaId, startAt, endAt, blockName, artist, information, blockInfoRedirectUrl);
    }

    private void validateDateTime(final LocalDateTime startAt, final LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new TimeTableIllegalArgumentException();
        }
    }
}