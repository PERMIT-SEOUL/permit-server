package com.permitseoul.permitserver.eventImage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_images")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class EventImage {
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
}

