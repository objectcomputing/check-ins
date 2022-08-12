package com.objectcomputing.checkins.auth.exceptions;

public class PlatformRuntimeException extends RuntimeException {
    public PlatformRuntimeException(String message) {
        super(message);
    }

    public PlatformRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
