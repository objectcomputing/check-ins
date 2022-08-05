package com.objectcomputing.checkins.services.onboard;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.gmail.model.Profile;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/onboardee-profiles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "onboardee profiles")
public class OnboardeeProfileController {
    private static final Logger LOG = LoggerFactory.getLogger(OnboardeeProfileServices.class);
    private final OnboardeeProfileServices onboardeeProfileServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public OnboardeeProfileController(OnboardeeProfileServices onboardeeProfileServices, EventLoopGroup eventLoopGroup,
            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.onboardeeProfileServices = onboardeeProfileServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutor(ioExecutorService);
    }

    // @Get("/{id}")
    // public Mono<HttpResponse<OnboardeeProfileDTO>> getById(UUID id) {
    //     return Mono.fromCallable(() -> onboardeeProfileServices.getById(id))
    //             .publishOn(Schedulers.fromExecutor(eventLoopGroup))
    //             .map(profile -> (HttpResponse<OnboardeeProfileDTO>) HttpResponse
    //                     .ok(fromEntity(profile))
    //                     .headers(headers -> headers.location(location(profile.getId()))))
    //             .subscribeOn(scheduler);
    // }
    
    // @Get("/{?id,firstName,lastName,postion,hireType,email,pdl}")
    // public Mono<HttpResponse<List<OnboardeeProfileDTO>>> findByValue(@Nullable UUID id,
    //                                                                 )
     
     
}
