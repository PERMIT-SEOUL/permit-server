package com.permitseoul.permitserver.domain.admin.event.api.controller;

import com.permitseoul.permitserver.domain.admin.event.api.dto.req.AdminEventWithTicketCreateRequest;
import com.permitseoul.permitserver.domain.admin.event.api.service.AdminEventService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    //어드민 행사 리스트 조회 API
    @GetMapping
    public ResponseEntity<BaseResponse<?>> getEvents(
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminEventService.getEvents());
    }

    //어드민 행사 리스트 조회 API
    @GetMapping("/{eventId}/details")
    public ResponseEntity<BaseResponse<?>> getEventDetail(
            @PathVariable(value = "eventId") long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminEventService.getEventDetail(eventId));
    }

    //어드민 행사+티켓 등록 API
    @PostMapping
    public ResponseEntity<BaseResponse<?>> createEvent(
            @RequestBody @Valid final AdminEventWithTicketCreateRequest adminEventWithTicketCreateRequest
            ) {
        adminEventService.createEventWithTickets(adminEventWithTicketCreateRequest);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
