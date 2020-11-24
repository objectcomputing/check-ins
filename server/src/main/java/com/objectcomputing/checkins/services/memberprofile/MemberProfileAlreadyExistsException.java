package com.objectcomputing.checkins.services.memberprofile;

public class MemberProfileAlreadyExistsException extends RuntimeException {
    public MemberProfileAlreadyExistsException(String message) {
        super(message);
    }
}