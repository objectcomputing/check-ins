package com.objectcomputing.checkins.services.exceptions;

public class BadArgException extends RuntimeException {
    public BadArgException(String message) {
        super(message);
    }
}
