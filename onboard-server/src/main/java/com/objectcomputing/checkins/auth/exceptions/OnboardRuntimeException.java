package com.objectcomputing.checkins.auth.exceptions;

public class OnboardRuntimeException extends RuntimeException {
    public OnboardRuntimeException() {
        super();
    }

    public OnboardRuntimeException(String message) {
        super(message);
    }

    public OnboardRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
