package com.objectcomputing.checkins.services.checkin_notes;

public class CheckinNotesBadArgException extends RuntimeException {
    public CheckinNotesBadArgException(String message) {
        super(message);
    }
}