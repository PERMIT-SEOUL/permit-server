package com.permitseoul.permitserver.domain.admin.timetable.base.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req.TimetableInitialPostRequest;
import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req.TimetableUpdateRequest;
import com.permitseoul.permitserver.domain.admin.timetable.base.api.service.AdminTimetableService;
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
public class AdminTimetableController {
    private final AdminTimetableService adminTimetableService;

    // admin 행사 타임테이블 최초 등록 API
    @PostMapping("/{eventId}/timetables/initial")
    public ResponseEntity<BaseResponse<?>> postInitialTimetableInfo(
            @PathVariable("eventId") final long eventId,
            @RequestBody @Valid TimetableInitialPostRequest request
    ) {
        adminTimetableService.saveInitialTimetableInfo(
                eventId,
                request.timetableStartAt(),
                request.timetableEndAt(),
                request.notionTimetableDataSourceId(),
                request.notionStageDataSourceId(),
                request.notionCategoryDataSourceId()
        );
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    // admin 행사 타임테이블 조회 API
    @GetMapping("/{eventId}/timetables")
    public ResponseEntity<BaseResponse<?>> getTimetable(
            @PathVariable("eventId") final long eventId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, adminTimetableService.getTimetableInfo(eventId));
    }

    // admin 행사 타임테이블 수정 API
    @PatchMapping("/timetables/{timetableId}")
    public ResponseEntity<BaseResponse<?>> updateTimetable(
            @PathVariable("timetableId") final long timetableId,
            @RequestBody TimetableUpdateRequest timetableUpdateRequest
    ) {
        adminTimetableService.updateTimetable(
                timetableId,
                timetableUpdateRequest.timetableStartAt(),
                timetableUpdateRequest.timetableEndAt(),
                timetableUpdateRequest.notionTimetableDataSourceId(),
                timetableUpdateRequest.notionStageDataSourceId(),
                timetableUpdateRequest.notionCategoryDataSourceId());
        return ApiResponseUtil.success(SuccessCode.OK);

    }
}
