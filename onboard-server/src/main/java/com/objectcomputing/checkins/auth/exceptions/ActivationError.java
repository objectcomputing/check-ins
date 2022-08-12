package com.objectcomputing.checkins.auth.exceptions;

import com.objectcomputing.checkins.auth.exceptions.PlatformRuntimeException;

public class ActivationError extends PlatformRuntimeException {
    private final String code;

    public ActivationError(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
