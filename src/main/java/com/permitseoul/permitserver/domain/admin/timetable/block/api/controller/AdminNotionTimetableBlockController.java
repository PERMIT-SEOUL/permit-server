package com.permitseoul.permitserver.domain.admin.timetable.block.api.controller;

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

    // Notion Timetable Block Database Update Webhook API
    @PostMapping("/timetables/blocks")
    public ResponseEntity<BaseResponse<?>> updateNotionTimetableBlockWebhook(
            @RequestBody @Valid NotionTimetableBlockUpdateWebhookRequest request
    ) {
        adminNotionTimetableBlockService.updateNotionTimetableBlock(request);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
