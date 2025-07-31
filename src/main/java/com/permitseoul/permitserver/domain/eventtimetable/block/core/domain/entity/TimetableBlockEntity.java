package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity;

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

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "block_name", nullable = false)
    private String blockName;

    @Column(name = "artist")
    private String artist;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "information", nullable = false)
    private String information;

    @Column(name = "block_info_redirect_url")
    private String blockInfoRedirectUrl;


    private TimetableBlockEntity(
            long timetableId,
            long timetableCategoryId,
            long timetableAreaId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String blockName,
            String artist,
            String imageUrl,
            String information,
            String blockInfoRedirectUrl
    ) {
        this.timetableId = timetableId;
        this.timetableCategoryId = timetableCategoryId;
        this.timetableAreaId = timetableAreaId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.blockName = blockName;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.information = information;
        this.blockInfoRedirectUrl = blockInfoRedirectUrl;
    }

    public static TimetableBlockEntity create(final long timetableId,
                                              final long timetableCategoryId,
                                              final long timetableAreaId,
                                              final LocalDateTime startDate,
                                              final LocalDateTime endDate,
                                              final String blockName,
                                              final String artist,
                                              final String imageUrl,
                                              final String information,
                                              final String blockInfoRedirectUrl) {
        return new TimetableBlockEntity(timetableId, timetableCategoryId, timetableAreaId, startDate, endDate, blockName, artist, imageUrl, information, blockInfoRedirectUrl);
    }
}