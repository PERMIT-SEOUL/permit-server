package com.permitseoul.permitserver.domain.auth.core.exception;

import lombok.Getter;

@Getter
public class AuthPlatformFeignException extends AuthCoreException {
    private final String platformErrorCode;

    public AuthPlatformFeignException(String platformErrorCode) {
        this.platformErrorCode = platformErrorCode;
    }

}
