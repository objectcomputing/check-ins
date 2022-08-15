package com.objectcomputing.checkins.services.onboard.exceptions.handlers;

import com.objectcomputing.checkins.services.onboard.exceptions.BadArgException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {BadArgException.class, ExceptionHandler.class})
public class ArgumentsHandler implements ExceptionHandler<BadArgException, HttpResponse> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, BadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest().body(error);
    }

}
