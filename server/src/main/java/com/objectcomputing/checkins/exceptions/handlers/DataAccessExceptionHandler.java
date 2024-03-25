package com.objectcomputing.checkins.exceptions.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
@Requires(classes = {DataAccessException.class, ExceptionHandler.class})
public class DataAccessExceptionHandler implements ExceptionHandler<DataAccessException, HttpResponse<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(DataAccessExceptionHandler.class);
    @Override
    public HttpResponse<?> handle(HttpRequest request, DataAccessException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        LOG.error(e.getMessage());
        return HttpResponse.<JsonError>status(HttpStatus.BAD_REQUEST).body(error);
    }

}
