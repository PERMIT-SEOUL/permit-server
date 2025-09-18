package com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_block_media")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimetableBlockMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_block_media_id", nullable = false)
    private Long timetableBlockMediaId;

    @Column(name = "timetable_block_id", nullable = false)
    @Positive(message = "timetableBlockId는 1이상이어야합니다.")
    private long timetableBlockId;

    @Column(name = "sequence", nullable = false)
    @Min(value = 0, message = "timetable_block_media의 sequence는 0이상이어야합니다.")
    private int sequence;

    @Column(name = "mediaUrl", nullable = false)
    private String mediaUrl;

    private TimetableBlockMediaEntity(long timetableBlockId, int sequence, String mediaUrl) {
        this.timetableBlockId = timetableBlockId;
        this.sequence = sequence;
        this.mediaUrl = mediaUrl;
    }
}
