package com.permitseoul.permitserver.domain.admin.util.exception;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class QrCodeException extends AdminApiException {
    public QrCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
