package com.objectcomputing.checkins.services.action_item;

import java.util.List;

public class ActionItemsBulkLoadException extends RuntimeException {
    private final List<String> errors;
    public ActionItemsBulkLoadException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
