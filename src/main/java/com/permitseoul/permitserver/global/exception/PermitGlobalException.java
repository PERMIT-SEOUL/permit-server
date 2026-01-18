package com.permitseoul.permitserver.global.exception;

public abstract class PermitGlobalException extends RuntimeException {
    protected PermitGlobalException() {
        super();
    }

    protected PermitGlobalException(final String message) {
        super(message);
    }

    protected PermitGlobalException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
