package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class SignRequestRetrievalException extends RuntimeException {
    public SignRequestRetrievalException(String message) { super(message); }
}
