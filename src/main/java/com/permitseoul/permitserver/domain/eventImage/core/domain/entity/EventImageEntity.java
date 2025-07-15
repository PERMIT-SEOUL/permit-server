package com.permitseoul.permitserver.domain.eventImage.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "is_thumbnail", nullable = false)
    private boolean isThumbnail;

    private EventImageEntity(long eventId, String imageUrl, int sequence, boolean isThumbnail) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
        this.isThumbnail = isThumbnail;
    }
}

