package com.objectcomputing.checkins.services.exceptions.handlers;

import com.objectcomputing.checkins.services.exceptions.PermissionException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {PermissionException.class, ExceptionHandler.class})
public class PermissionsHandler implements ExceptionHandler<PermissionException, HttpResponse> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, PermissionException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>unauthorized().body(error);
    }

}
