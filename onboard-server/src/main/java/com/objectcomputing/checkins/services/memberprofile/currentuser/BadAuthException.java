package com.objectcomputing.checkins.services.memberprofile.currentuser;

public class BadAuthException extends RuntimeException {
    public BadAuthException(String message) {
        super(message);
    }
}
