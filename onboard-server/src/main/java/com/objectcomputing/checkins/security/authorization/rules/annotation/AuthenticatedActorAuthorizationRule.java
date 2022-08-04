package com.objectcomputing.checkins.security.authorization.rules.annotation;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;

@Singleton
public class AuthenticatedActorAuthorizationRule implements AnnotationAuthorizationRule {
    private static final String NAME = "HasAuthenticatedActor";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean check(HttpRequest<?> request, AuthenticatedActor authentication) {
        return null != authentication && null != authentication.getTicket();
    }
}
