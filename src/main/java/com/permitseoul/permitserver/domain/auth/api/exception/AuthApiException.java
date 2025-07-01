package com.permitseoul.permitserver.domain.auth.api.exception;

import com.permitseoul.permitserver.domain.auth.AuthBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthApiException extends AuthBaseException {
  private final ErrorCode errorCode;

}
