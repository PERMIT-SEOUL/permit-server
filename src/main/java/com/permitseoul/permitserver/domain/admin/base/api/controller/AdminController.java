package com.permitseoul.permitserver.domain.admin.base.api.controller;

import com.permitseoul.permitserver.domain.admin.base.api.dto.req.AdminValidateRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.req.S3PreSignedUrlRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.req.UserAuthorityGetRequest;
import com.permitseoul.permitserver.domain.admin.base.api.dto.res.UserAuthorityUpdateRequest;
import com.permitseoul.permitserver.domain.admin.base.api.service.AdminService;
import com.permitseoul.permitserver.domain.admin.util.aws.S3Service;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final AdminService adminService;
    private final S3Service s3Service;

    //접근 권한 검증 API
    @PostMapping("/validate")
    public ResponseEntity<BaseResponse<?>> validateAdminCode(
            @RequestBody @Valid final AdminValidateRequest adminValidateRequest
    ) {
        adminService.validateAdminCode(adminValidateRequest.adminCode());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    // pre-signed Url 조회 API
    @PostMapping("/images/url")
    public ResponseEntity<BaseResponse<?>> getUploadImagesUrl(
            @RequestBody @Valid final S3PreSignedUrlRequest request
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, s3Service.getS3PreSignedUrls(request.eventId(), request.mediaInfoRequests()));
    }

    //유저 권한 정보 조회 API
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<?>> getUserAuthority(
            @RequestParam("email")
            @NotBlank(message = "email은 필수입니다.")
            @Email(message = "email 형식이 올바르지 않습니다.")
            final String email
    ){
        return ApiResponseUtil.success(SuccessCode.OK, adminService.getUserAuthority(email));
    }

    //유저 권한 변경 API
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<BaseResponse<?>> updateUserAuthority(
            @PathVariable("userId") final long userId,
            @RequestBody @Valid final UserAuthorityUpdateRequest request
    ) {
        adminService.updateUserAuthority(userId, request.role());
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
