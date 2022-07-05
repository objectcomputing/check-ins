package com.objectcomputing.checkins.services.file;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class FileRetrievalException extends RuntimeException {
    public FileRetrievalException(String message) {
        super(message);
    }
}
