package com.objectcomputing.checkins.services.agenda_item;

public class AgendaItemBadArgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AgendaItemBadArgException(String message) {
        super(message);
    }
}