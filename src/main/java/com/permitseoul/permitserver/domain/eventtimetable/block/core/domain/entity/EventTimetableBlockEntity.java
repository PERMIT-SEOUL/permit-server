package com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_timetable_block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventTimetableBlockEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_timetable_block_id", nullable = false)
    private Long eventTimetableBlockId;

    @Column(name = "event_timetable_id", nullable = false)
    private long eventTimetableId;

    @Column(name = "event_timetable_category_id", nullable = false)
    private long eventTimetableCategoryId;

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

    private EventTimetableBlockEntity(
            long eventTimetableId,
            long eventTimetableCategoryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String blockName,
            String artist,
            String imageUrl,
            String information
    ) {
        this.eventTimetableId = eventTimetableId;
        this.eventTimetableCategoryId = eventTimetableCategoryId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.blockName = blockName;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.information = information;
    }

    public static EventTimetableBlockEntity create(final long eventTimetableId,
                                                   final long eventTimetableCategoryId,
                                                   final LocalDateTime startDate,
                                                   final LocalDateTime endDate,
                                                   final String blockName,
                                                   final String artist,
                                                   final String imageUrl,
                                                   final String information) {
        return new EventTimetableBlockEntity(eventTimetableId, eventTimetableCategoryId, startDate, endDate, blockName, artist, imageUrl, information);
    }
}