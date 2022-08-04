package com.objectcomputing.checkins.security.authorization.rules.annotation;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;


public interface AnnotationAuthorizationRule extends Ordered {
    String getName();
    boolean check(HttpRequest<?> request, @Nullable AuthenticatedActor authentication);
}
