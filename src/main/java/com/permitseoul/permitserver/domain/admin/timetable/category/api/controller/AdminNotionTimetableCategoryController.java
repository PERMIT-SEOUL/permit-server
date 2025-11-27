package com.permitseoul.permitserver.domain.admin.timetable.category.api.controller;

import com.permitseoul.permitserver.domain.admin.timetable.category.api.dto.NotionTimetableCategoryUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.category.api.service.AdminNotionTimetableCategoryService;
import com.permitseoul.permitserver.domain.admin.timetable.stage.api.dto.NotionTimetableStageUpdateWebhookRequest;
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
public class AdminNotionTimetableCategoryController {
    private final AdminNotionTimetableCategoryService adminNotionTimetableCategoryService;

    // Notion 타임테이블 category 데이터베이스 Update Webhook API
    @PostMapping("/timetables/categories")
    public ResponseEntity<BaseResponse<?>> updateNotionTimetableCategoryWebhook(
            @RequestBody @Valid final NotionTimetableCategoryUpdateWebhookRequest notionTimetableCategoryUpdateWebhookRequest
    ) {
        adminNotionTimetableCategoryService.updateNotionTimetableCategory(notionTimetableCategoryUpdateWebhookRequest);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
