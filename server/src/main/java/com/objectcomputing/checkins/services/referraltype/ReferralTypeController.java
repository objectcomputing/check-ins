package com.objectcomputing.checkins.services.referraltype;

import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailabilityController;
import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailabilityDTO;
import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailabilityServices;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/referral-type")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "referral type")
public class ReferralTypeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmploymentDesiredAvailabilityController.class);

    private final ReferralTypeServices referralTypeServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public ReferralTypeController(ReferralTypeServices referralTypeServices,
                                                   EventLoopGroup eventLoopGroup,
                                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.referralTypeServices = referralTypeServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = (Scheduler) Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find referral type based on onboardee profile id.
     *
     * @param id {@link UUID} ID of the onboardee profile's referral type
     * @return {@link ReferralTypeDTO } Returned onboardee profile's referral type
     */
    @Get("/{id}")
    public Mono<HttpResponse<ReferralTypeDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> referralTypeServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<EmploymentDesiredAvailabilityDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }
}
