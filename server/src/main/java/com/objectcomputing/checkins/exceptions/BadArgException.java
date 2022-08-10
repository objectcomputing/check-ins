package com.objectcomputing.checkins.exceptions;

public class BadArgException extends RuntimeException {
    public BadArgException(String message) {
        super(message);
    }

    public BadArgException(String message, Object... args) {
        super(String.format(message, args));
    }
}
