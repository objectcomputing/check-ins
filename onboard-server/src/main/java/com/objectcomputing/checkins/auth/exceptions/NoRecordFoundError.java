package com.objectcomputing.checkins.auth.exceptions;

public class NoRecordFoundError extends PlatformRuntimeException {
    private final String code;

    public NoRecordFoundError(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
