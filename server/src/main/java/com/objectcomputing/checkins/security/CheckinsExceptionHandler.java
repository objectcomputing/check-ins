package com.objectcomputing.checkins.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Singleton
public class CheckinsExceptionHandler implements ExceptionHandler<InsufficientPrivelegesException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, InsufficientPrivelegesException exception) {
        return HttpResponse.status(HttpStatus.FORBIDDEN);
    }
}
