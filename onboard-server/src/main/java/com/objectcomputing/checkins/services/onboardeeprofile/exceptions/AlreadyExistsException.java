package com.objectcomputing.checkins.services.onboardeeprofile.exceptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
