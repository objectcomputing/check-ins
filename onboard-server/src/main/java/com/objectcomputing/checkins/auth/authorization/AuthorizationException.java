package com.objectcomputing.checkins.auth.authorization;

import com.objectcomputing.checkins.auth.exceptions.OnboardRuntimeException;
import com.objectcomputing.checkins.security.authentication.AuthenticatedActor;
import io.micronaut.core.annotation.Nullable;

public class AuthorizationException extends RuntimeException {
    private static final String AUTHORIZATION_FORBIDDEN_MESSAGE = "authorization forbidden";
    private static final String AUTHORIZATION_REJECTED_MESSAGE = "authorization rejected";

    private final AuthenticatedActor<?,?,?> authenticatedActor;
    private final boolean forbidden;

    /**
     * @param authenticatedActor The authentication that was denied, null if unauthorized
     */
    public AuthorizationException(@Nullable AuthenticatedActor<?,?,?> authenticatedActor) {
        this.authenticatedActor = authenticatedActor;
        this.forbidden = authenticatedActor != null;
    }

    /**
     * @return True if the request was authenticated
     */
    public boolean isForbidden() {
        return forbidden;
    }

    /**
     * @return The authenticated actor identified in the request
     */
    @Nullable
    public AuthenticatedActor<?,?,?> getAuthenticatedActor() {
        return authenticatedActor;
    }

    @Override
    public String getMessage() {
        return isForbidden() ? AUTHORIZATION_FORBIDDEN_MESSAGE : AUTHORIZATION_REJECTED_MESSAGE;
    }
}