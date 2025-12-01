package com.permitseoul.permitserver.domain.admin.timetable.block.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockCreatedWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.api.service.AdminNotionTimetableBlockService;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notion/events")
public class AdminNotionTimetableBlockController {
    private final AdminNotionTimetableBlockService adminNotionTimetableBlockService;

    // Notion 타임테이블 블럭 데이터베이스 필드 Update Webhook API
    @PostMapping("/timetables/blocks")
    public ResponseEntity<BaseResponse<?>> updateNotionTimetableBlockWebhook(
            @RequestBody @Valid NotionTimetableBlockUpdateWebhookRequest webhookRequest
    ) {
        adminNotionTimetableBlockService.updateNotionTimetableBlock(webhookRequest);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    //Notion 타임테이블 블럭 데이터베이스 페이지 추가 Webhook API
    @PostMapping("/timetables")
    public ResponseEntity<BaseResponse<?>> addNotionTimetableBlockPage(
            @RequestBody @Valid NotionTimetableBlockCreatedWebhookRequest webhookRequest
    ) {
        adminNotionTimetableBlockService.saveNewTimetableBlockRowWebhookRequest(webhookRequest);
        return ApiResponseUtil.success(SuccessCode.OK);

    }
}
