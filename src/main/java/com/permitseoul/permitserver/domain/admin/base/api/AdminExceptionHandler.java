package com.permitseoul.permitserver.domain.admin.base.api;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.permitseoul.permitserver.domain.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(AdminApiException.class)
    public ResponseEntity<BaseResponse<?>> handleAdminApiException(final AdminApiException e) {
        return ApiResponseUtil.failure(e.getErrorCode());
    }


}
