package com.permitseoul.permitserver.global.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PermitApiBaseException extends PermitBaseException {
  private final ErrorCode errorCode;

}
