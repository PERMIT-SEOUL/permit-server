package com.permitseoul.permitserver.domain.admin.api.exception;

import com.permitseoul.permitserver.domain.admin.AdminBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminApiException extends AdminBaseException {
    private final ErrorCode errorCode;

}
