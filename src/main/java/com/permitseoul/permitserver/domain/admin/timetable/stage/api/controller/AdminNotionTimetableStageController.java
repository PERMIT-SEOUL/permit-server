package com.permitseoul.permitserver.domain.admin.timetable.stage.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.stage.api.service.AdminNotionTimetableStageService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notion/events")
public class AdminNotionTimetableStageController {

    private final AdminNotionTimetableStageService adminNotionTimetableStageService;

    // Notion 타임테이블 stage 데이터베이스 Update Webhook API
    @PostMapping("/timetables/stages")
    public ResponseEntity<BaseResponse<?>> updateNotionTimetableStageWebhook(
            @RequestBody @Valid final NotionTimetableStageUpdateWebhookRequest notionTimetableStageUpdateWebhookRequest
    ) {
        adminNotionTimetableStageService.updateNotionTimetableStage(notionTimetableStageUpdateWebhookRequest);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
