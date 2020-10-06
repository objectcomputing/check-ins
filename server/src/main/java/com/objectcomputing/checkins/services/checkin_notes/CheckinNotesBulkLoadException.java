package com.objectcomputing.checkins.services.checkin_notes;

import java.util.List;

public class CheckinNotesBulkLoadException extends RuntimeException {
    private final List<String> errors;
    public CheckinNotesBulkLoadException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}