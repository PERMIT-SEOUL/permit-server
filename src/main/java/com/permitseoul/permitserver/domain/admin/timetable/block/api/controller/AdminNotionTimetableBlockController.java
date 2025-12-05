package com.permitseoul.permitserver.domain.admin.timetable.block.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.base.api.dto.req.NotionTimetableCreatedNewRowWebhookRequest;
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
    @PostMapping("/timetables/blocks/update")
    public ResponseEntity<BaseResponse<?>> updateNotionTimetableBlockWebhook(
            @RequestBody NotionTimetableBlockUpdateWebhookRequest webhookRequest
    ) {
        adminNotionTimetableBlockService.updateNotionTimetableBlock(webhookRequest);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    //Notion 타임테이블 블럭 데이터베이스 페이지 추가 Webhook API
    @PostMapping("/timetables/blocks/new")
    public ResponseEntity<BaseResponse<?>> addNotionTimetableBlockRow(
            @RequestBody NotionTimetableCreatedNewRowWebhookRequest webhookRequest
    ) {
        adminNotionTimetableBlockService.saveNewTimetableBlockRowWebhookRequest(webhookRequest.data().parent().dataSourceId(), webhookRequest.data().id());
        return ApiResponseUtil.success(SuccessCode.OK);

    }
}
