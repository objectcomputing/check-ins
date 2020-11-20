package com.objectcomputing.checkins.services.team;

public class TeamNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "No such team found";
    public TeamNotFoundException() {this(DEFAULT_MESSAGE);}
    public TeamNotFoundException(String message) { super(message); }
}
