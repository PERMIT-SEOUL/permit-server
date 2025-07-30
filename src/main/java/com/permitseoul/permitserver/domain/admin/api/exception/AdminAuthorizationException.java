package com.permitseoul.permitserver.domain.admin.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class AdminAuthorizationException extends AdminApiException {
  public AdminAuthorizationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
