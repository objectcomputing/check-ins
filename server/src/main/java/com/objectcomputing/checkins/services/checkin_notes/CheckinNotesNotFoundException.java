package com.objectcomputing.checkins.services.checkin_notes;

public class CheckinNotesNotFoundException extends RuntimeException {
    public CheckinNotesNotFoundException(String message) {
        super(message);
    }
}