package com.permitseoul.permit_server.eventImage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_images")
public class EventImage {
    @Id
    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sequence", nullable = false)
    private int sequence;
}

