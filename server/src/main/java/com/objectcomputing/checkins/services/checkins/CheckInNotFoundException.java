package com.objectcomputing.checkins.services.checkins;

public class CheckInNotFoundException extends RuntimeException {
    public CheckInNotFoundException(String message) { super(message); }
}
