package com.permitseoul.permitserver.domain.user.api.controller;

import com.permitseoul.permitserver.domain.user.api.dto.UserEmailCheckRequest;
import com.permitseoul.permitserver.domain.user.api.service.UserService;
import com.permitseoul.permitserver.global.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    //유저 이메일 중복 체크 API
    @PostMapping("/email-check")
    public ResponseEntity<BaseResponse<?>> checkEmailDuplicated(
            @RequestBody @Valid final UserEmailCheckRequest userEmailCheckRequest
            ) {
        userService.checkEmailDuplicated(userEmailCheckRequest.userEmail());
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<?>> getUserInfo(
            @UserIdHeader final Long userId
    ) {
        return ApiResponseUtil.success(SuccessCode.OK, userService.getUserInfo(userId));
    }
}



