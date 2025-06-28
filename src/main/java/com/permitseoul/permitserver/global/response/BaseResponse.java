package com.permitseoul.permitserver.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.permitseoul.permitserver.global.response.code.ApiCode;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE) ///무분별한 객체 생성을 차단 + 빌더나 정적 팩토리 메서드 패턴 강제
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE) ///외부에서 new로 생성 막고 + @Builder만 사용하여 내부 구현에서만 사용
@Builder(access = lombok.AccessLevel.PRIVATE) ///빌더 사용은 내부에서만 허용
public class BaseResponse<T> {
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) ///JSON 직렬화(응답 변환) 할 때 null인 필드는 아예 빼버림
    private T data;

    public static BaseResponse<?> of(final ApiCode apiMessage) {
        return BaseResponse.builder()
                .status(apiMessage.getCode())
                .message(apiMessage.getMessage())
                .build();
    }

    public static <T> BaseResponse<?> of(final SuccessCode successCode, final T data) {
        return BaseResponse.builder()
                .status(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    //error 메시지 따로 넣어줄 때, 사용
    public static BaseResponse<?> of(final ErrorCode errorCode, final String message) {
        return BaseResponse.builder()
                .status(errorCode.getCode())
                .message(message)
                .build();
    }
}