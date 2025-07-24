package com.permitseoul.permitserver.domain.event.api.exception;

import com.permitseoul.permitserver.domain.event.EventBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventApiException extends EventBaseException {
  private final ErrorCode errorCode;

}
