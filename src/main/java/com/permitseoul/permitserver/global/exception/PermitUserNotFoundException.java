package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PermitUserNotFoundException extends PermitBaseException {
    private final ErrorCode errorCode;
    private final String socialAccessToken;

}
