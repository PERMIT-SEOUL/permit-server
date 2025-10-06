package com.permitseoul.permitserver.domain.admin.base.api.controller;

import com.permitseoul.permitserver.domain.admin.base.api.dto.AdminValidateRequest;
import com.permitseoul.permitserver.domain.admin.base.api.service.AdminService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    //접근 권한 검증 API
    @PostMapping("/validate")
    public ResponseEntity<BaseResponse<?>> validateAdminCode(
            @RequestBody @Valid final AdminValidateRequest adminValidateRequest
    ) {
        adminService.validateAdminCode(adminValidateRequest.adminCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    // pre-signed Url 조회 API





}
