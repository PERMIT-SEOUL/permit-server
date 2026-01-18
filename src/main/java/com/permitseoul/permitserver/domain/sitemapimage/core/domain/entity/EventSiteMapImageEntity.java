package com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "event_sitemap_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSiteMapImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sitemap_images_id", nullable = false)
    private Long siteMapImagesId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "sitemap_image_url", nullable = false, columnDefinition = "TEXT")
    private String sitemapImageUrl;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    private EventSiteMapImageEntity(final String sitemapImageUrl,
                                    final int sequence,
                                    final long eventId
    ) {
        this.sequence = sequence;
        this.sitemapImageUrl = sitemapImageUrl;
        this.eventId = eventId;
    }

    public static EventSiteMapImageEntity create(final String sitemapImageUrl,
                                                 final int sequence,
                                                 final long eventId) {
        return new EventSiteMapImageEntity( sitemapImageUrl, sequence, eventId);
    }
}
