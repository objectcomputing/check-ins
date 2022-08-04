package com.objectcomputing.geoai.platform.account.endpoint;

import com.objectcomputing.geoai.platform.account.exceptions.AccountManagementError;
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
@Requires(classes = {AccountManagementError.class, ExceptionHandler.class})
public class AccountManagementErrorHandler  implements ExceptionHandler<AccountManagementError, HttpResponse> {
    @Override
    public HttpResponse handle(HttpRequest request, AccountManagementError exception) {
        return HttpResponse.status(HttpStatus.FAILED_DEPENDENCY)
                .body(Map.of("success", Boolean.FALSE,
                        "code",    exception.getCode(),
                        "reason",  exception.getMessage()))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
