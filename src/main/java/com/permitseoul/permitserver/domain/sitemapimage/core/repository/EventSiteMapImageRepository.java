package com.permitseoul.permitserver.domain.sitemapimage.core.repository;

import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventSiteMapImageRepository extends JpaRepository<EventSiteMapImageEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from EventSiteMapImageEntity e where e.eventId = :eventId")
    void deleteAllByEventId(@Param("eventId") long eventId);

    List<EventSiteMapImageEntity> findAllByEventId(final long eventId);
}
