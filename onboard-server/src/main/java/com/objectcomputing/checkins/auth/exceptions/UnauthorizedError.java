package com.objectcomputing.checkins.auth.exceptions;

import com.objectcomputing.checkins.auth.exceptions.PlatformRuntimeException;

public class UnauthorizedError extends PlatformRuntimeException {
    public UnauthorizedError(String message) {
        super(message);
    }

    public UnauthorizedError(String message, Throwable cause) {
        super(message, cause);
    }
}
