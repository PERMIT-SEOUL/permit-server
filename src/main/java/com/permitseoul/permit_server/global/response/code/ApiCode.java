package com.permitseoul.permit_server.global.response.code;

import org.springframework.http.HttpStatus;

public interface ApiCode {
    HttpStatus getHttpStatus();
    int getStatus();
    String getMessage();
}
