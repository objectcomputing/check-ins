package com.objectcomputing.checkins.services.guild;

import java.util.List;

public class GuildBulkLoadException extends RuntimeException {
    private final List<String> errors;
    public GuildBulkLoadException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}