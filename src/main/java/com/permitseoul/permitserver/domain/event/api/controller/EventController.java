package com.permitseoul.permitserver.domain.event.api.controller;


import com.permitseoul.permitserver.domain.event.api.dto.EventAllResponse;
import com.permitseoul.permitserver.domain.event.api.service.EventService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    // 행사 전체 조회 api
    @PostMapping()
    public ResponseEntity<BaseResponse<?>> getAllEvents() {
        final EventAllResponse eventAllResponse = eventService.getAllVisibleEvents();
        return ApiResponseUtil.success(SuccessCode.OK, eventService);
    }
}
