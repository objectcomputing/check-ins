package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.geoai.platform.auth.exceptions.AuthenticationError;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.util.Map;

@Produces
@Singleton
@Requires(classes = {AuthenticationError.class, ExceptionHandler.class})
public class AuthenticationErrorHandler implements ExceptionHandler<AuthenticationError, HttpResponse> {
    @Override
    public HttpResponse handle(HttpRequest request, AuthenticationError exception) {
        return HttpResponse.status(HttpStatus.FAILED_DEPENDENCY)
                .body(Map.of("success", Boolean.FALSE,
                             "reason", exception.getMessage()))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
