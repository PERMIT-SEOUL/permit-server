package com.permitseoul.permitserver.domain.admin.base.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class AdminAuthorizationException extends AdminApiException {
  public AdminAuthorizationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
