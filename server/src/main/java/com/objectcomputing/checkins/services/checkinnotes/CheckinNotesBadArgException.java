package com.objectcomputing.checkins.services.checkinnotes;

public class CheckinNotesBadArgException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CheckinNotesBadArgException(String message) {
        super(message);
    }
}