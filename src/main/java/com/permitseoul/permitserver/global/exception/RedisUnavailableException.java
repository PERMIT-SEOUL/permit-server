package com.permitseoul.permitserver.global.exception;

public class RedisUnavailableException extends PermitGlobalException {
    public RedisUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RedisUnavailableException(final String message) {
        super(message);
    }
}
