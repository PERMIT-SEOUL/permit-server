package com.permitseoul.permitserver.domain.admin.event.api.controller;

import com.permitseoul.permitserver.domain.admin.event.api.service.AdminEventService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getEvents(
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminEventService.getEvents());
    }

    @GetMapping("/{eventId}/details")
    public ResponseEntity<BaseResponse<?>> getEventDetail(
            @PathVariable(value = "eventId") long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminEventService.getEventDetail(eventId));
    }
}
