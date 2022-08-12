package com.objectcomputing.checkins.auth.exceptions;

import com.objectcomputing.checkins.auth.exceptions.PlatformRuntimeException;

public class AuthenticationError extends PlatformRuntimeException {
    public AuthenticationError(String message) {
        super(message);
    }

    public AuthenticationError(String message, Throwable cause) {
        super(message, cause);
    }
}
