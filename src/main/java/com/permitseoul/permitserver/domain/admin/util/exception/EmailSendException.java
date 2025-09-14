package com.permitseoul.permitserver.domain.admin.util.exception;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class EmailSendException extends AdminApiException {
    public EmailSendException(ErrorCode errorCode) {
        super(errorCode);
    }
}
