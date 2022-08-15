package com.objectcomputing.checkins.services.onboard.exceptions.handlers;

import com.objectcomputing.checkins.services.onboard.exceptions.PermissionException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {PermissionException.class, ExceptionHandler.class})
public class PermissionsHandler implements ExceptionHandler<PermissionException, HttpResponse> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, PermissionException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.status(HttpStatus.FORBIDDEN).body(error);
    }

}
