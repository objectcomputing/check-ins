package com.objectcomputing.checkins.services.memberprofile.currentuser;

public class NoSuchProfileException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "no member profile exists for this email";
    public NoSuchProfileException() {
        super(DEFAULT_MESSAGE);
    }
}
