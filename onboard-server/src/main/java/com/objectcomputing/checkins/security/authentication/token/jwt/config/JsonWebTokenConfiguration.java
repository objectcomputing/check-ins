package com.objectcomputing.checkins.security.authentication.token.jwt.config;

import io.micronaut.core.util.Toggleable;

public interface JsonWebTokenConfiguration extends Toggleable {

    String getIssuer();
    default long getLease() { return 36000L; }
}
