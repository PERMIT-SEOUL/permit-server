package com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "event_sitemap_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSiteMapImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sitemap_images_id", nullable = false)
    public Long siteMapImagesId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "sitemap_image_url", nullable = false, columnDefinition = "TEXT")
    private String sitemapImageUrl;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    private EventSiteMapImageEntity(final long siteMapImagesId,
                                    final int sequence,
                                    final String sitemapImageUrl,
                                    final long eventId
    ) {
        this.siteMapImagesId = siteMapImagesId;
        this.sequence = sequence;
        this.sitemapImageUrl = sitemapImageUrl;
        this.eventId = eventId;
    }

    public static EventSiteMapImageEntity create(final long siteMapImagesId,
                                                 final int sequence,
                                                 final String sitemapImageUrl,
                                                 final long eventId) {
        return new EventSiteMapImageEntity(siteMapImagesId, sequence, sitemapImageUrl, eventId);
    }
}
