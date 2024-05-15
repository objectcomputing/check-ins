package com.objectcomputing.checkins.services.request_notifications;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.annotation.security.PermitAll;
import reactor.core.publisher.Mono;

@Controller("/services/feedback/daily-request-check")
@ExecuteOn(TaskExecutors.IO)
@PermitAll
public class CheckServicesController {

    private final CheckServices checkServices;
    private final ServiceAccountVerifier serviceAccountVerifier;

    public CheckServicesController(CheckServices checkServices, ServiceAccountVerifier serviceAccountVerifier) {
        this.checkServices = checkServices;
        this.serviceAccountVerifier = serviceAccountVerifier;
    }

    @Get
    public Mono<? extends HttpResponse<?>> sendScheduledEmails(@Header("Authorization") String authorizationHeader) {
        String authorization = authorizationHeader.split(" ")[1];
        serviceAccountVerifier.verify(authorization);
        return Mono.fromCallable(checkServices::sendScheduledEmails)
                .map(success -> (HttpResponse<?>) HttpResponse.ok());
    }

}
