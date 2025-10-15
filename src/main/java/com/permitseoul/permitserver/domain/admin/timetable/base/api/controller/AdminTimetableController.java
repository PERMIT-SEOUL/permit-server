package com.permitseoul.permitserver.domain.admin.timetable.base.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req.TimetableInitialPostRequest;
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

    // admin 타임테이블 최초 등록 API
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
}
