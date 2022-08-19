package com.objectcomputing.checkins.exceptions;

public class PermissionException extends RuntimeException {
    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Object... args) {
        super(String.format(message, args));
    }
}
