package com.permitseoul.permitserver.global.response;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {
    public static ResponseEntity<BaseResponse<?>> success(final SuccessCode successCode) {
        return org.springframework.http.ResponseEntity.status(successCode.getHttpStatus())
                .body(BaseResponse.of(successCode));
    }

    public static <T> ResponseEntity<BaseResponse<?>> success(final SuccessCode successCode, final T data) {
        return ResponseEntity.status(successCode.getHttpStatus())
                .body(BaseResponse.of(successCode, data));
    }

    public static ResponseEntity<BaseResponse<?>> failure(final ErrorCode errorBaseCode) {
        return ResponseEntity.status(errorBaseCode.getHttpStatus())
                .body(BaseResponse.of(errorBaseCode));
    }

    //따로 error message 넣어줄 때, 사용
    public static ResponseEntity<BaseResponse<?>> failure(final ErrorCode errorBaseCode, final String message) {
        return ResponseEntity
                .status(errorBaseCode.getHttpStatus())
                .body(BaseResponse.of(errorBaseCode.getCode(), message));
    }
}

