package com.permitseoul.permitserver.domain.sitemapimage.core.repository;

import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSiteMapImageRepository extends JpaRepository<EventSiteMapImageEntity, Long> {
}
