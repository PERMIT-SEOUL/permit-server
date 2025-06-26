package com.permitseoul.permitserver.global.response.code;

import org.springframework.http.HttpStatus;

public interface ApiCode {
    HttpStatus getHttpStatus();
    int getStatus();
    String getMessage();
}
