package com.objectcomputing.checkins.services.request_notifications;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/daily-request-check")
@PermitAll
public class CheckServicesController {


    private final CheckServices checkServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;
    private final ServiceAccountVerifier serviceAccountVerifier;

    public CheckServicesController(CheckServices checkServices, EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService, ServiceAccountVerifier serviceAccountVerifier) {
        this.checkServices = checkServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
        this.serviceAccountVerifier = serviceAccountVerifier;
    }

    @Get
    public Mono<? extends HttpResponse<?>> sendScheduledEmails(@Header("Authorization") String authorizationHeader) {
        String authorization = authorizationHeader.split(" ")[1];
        serviceAccountVerifier.verify(authorization);
        return Mono.fromCallable(checkServices::sendScheduledEmails)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
