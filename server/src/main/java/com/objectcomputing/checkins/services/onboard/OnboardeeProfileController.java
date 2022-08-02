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

@Controller("/services/onboard")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "onboardee profiles")
public class OnboardeeProfileController {
    // private static final Logger LOG = LoggerFactory.getLogger(EditOnboardeeProfileController.class);
    // private final EditOnboardeeProfileServices editOnboardeeProfileServices;
    // private final EventLoopGroup eventLoopGroup;
    // private final Scheduler scheduler;

    // public EditOnboardeeProfileController(EditOnboardeeProfileController editOnboardeeProfileController)
    //     //     EventLoopGroup eventLoopGroup,
    //     //     @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
    //     // //this.editOnboardeeProfileServices = editOnboardeeProfileServices;
    //     // this.eventLoopGroup = eventLoopGroup;
    //     // this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    // }

    

}
