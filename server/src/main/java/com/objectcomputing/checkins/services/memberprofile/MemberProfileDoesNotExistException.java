package com.objectcomputing.checkins.services.memberprofile;

public class MemberProfileDoesNotExistException extends RuntimeException {
    public MemberProfileDoesNotExistException(String message) {
        super(message);
    }
}
