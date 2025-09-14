package com.permitseoul.permitserver.domain.eventimage.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_image_id", nullable = false)
    private Long eventImageId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    private EventImageEntity(long eventId, String imageUrl, int sequence) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public static EventImageEntity create(final long eventId,
                                          final String imageUrl,
                                          final int sequence) {
        return new EventImageEntity(eventId, imageUrl, sequence);
    }
}

