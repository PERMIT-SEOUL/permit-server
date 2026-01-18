package com.permitseoul.permitserver.domain.sitemapimage.api.controller;

import com.permitseoul.permitserver.domain.sitemapimage.api.service.EventSiteMapImageService;
import com.permitseoul.permitserver.global.aop.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventSiteMapImageController {
    private final EventSiteMapImageService eventSiteMapImageService;

    //이벤트 siteMap 이미지 조회 API
    @GetMapping("/{eventId}/sitemap")
    public ResponseEntity<BaseResponse<?>> getEventSitemapImages (
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, eventSiteMapImageService.getEventSiteMapImages(eventId));
    }
}
