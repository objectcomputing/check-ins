package com.objectcomputing.checkins.security;

public class InsufficientPrivelegesException extends RuntimeException {
    public InsufficientPrivelegesException(String message) {
        super(message);
    }
}
