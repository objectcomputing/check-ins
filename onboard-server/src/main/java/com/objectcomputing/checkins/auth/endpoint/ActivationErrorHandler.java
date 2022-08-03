package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.geoai.platform.auth.exceptions.ActivationError;
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
@Requires(classes = {ActivationError.class, ExceptionHandler.class})
public class ActivationErrorHandler implements ExceptionHandler<ActivationError, HttpResponse> {
    @Override
    public HttpResponse handle(HttpRequest request, ActivationError exception) {
        return HttpResponse.status(HttpStatus.FAILED_DEPENDENCY)
                .body(Map.of("success", Boolean.FALSE,
                             "code",    exception.getCode(),
                             "reason",  exception.getMessage()))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
