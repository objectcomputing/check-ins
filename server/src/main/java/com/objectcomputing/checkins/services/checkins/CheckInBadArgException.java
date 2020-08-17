package com.objectcomputing.checkins.services.checkins;

public class CheckInBadArgException extends RuntimeException{
     /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CheckInBadArgException(String message) {
        super(message);
    }
}