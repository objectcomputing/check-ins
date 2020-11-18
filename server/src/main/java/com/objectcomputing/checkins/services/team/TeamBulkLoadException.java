package com.objectcomputing.checkins.services.team;

import java.util.List;

public class TeamBulkLoadException extends RuntimeException {
    private final List<String> errors;
    public TeamBulkLoadException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
