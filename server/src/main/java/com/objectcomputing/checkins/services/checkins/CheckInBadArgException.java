package com.objectcomputing.checkins.services.checkins;

public class CheckInBadArgException extends RuntimeException{

    public CheckInBadArgException(String message) {
        super(message);
    }
}