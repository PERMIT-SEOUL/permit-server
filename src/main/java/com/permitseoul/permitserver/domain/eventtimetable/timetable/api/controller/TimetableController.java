package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.controller;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service.TimetableService;
import com.permitseoul.permitserver.global.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class TimetableController {
    private final TimetableService timeTableService;

    //행사 타임테이블 전체 조회 API
    @GetMapping("/{eventId}/timetables")
    public ResponseEntity<BaseResponse<?>> getEventTimetable(
            @UserIdHeader final Long userId,
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, timeTableService.getEventTimetable(eventId, userId));
    }

    //행사 타임테이블 전체 조회 API
    @GetMapping("/timetables/{blockId}")
    public ResponseEntity<BaseResponse<?>> getEventTimetableDetail(
            @UserIdHeader final Long userId,
            @EventIdPathVariable final Long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, timeTableService.getEventTimetable(eventId, userId));
    }


}
