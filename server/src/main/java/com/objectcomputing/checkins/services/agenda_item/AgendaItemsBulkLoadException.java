package com.objectcomputing.checkins.services.agenda_item;

import java.util.List;

public class AgendaItemsBulkLoadException extends RuntimeException {
    private final List<String> errors;
    public AgendaItemsBulkLoadException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
