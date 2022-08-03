package com.objectcomputing.checkins.auth.exceptions;

import com.objectcomputing.checkins.auth.exceptions.PlatformRuntimeException;

public class AccountManagementError extends PlatformRuntimeException {
    private final String code;

    public AccountManagementError(String code, String message) {
        super(message);
        this.code = code;
    }

    public AccountManagementError(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
