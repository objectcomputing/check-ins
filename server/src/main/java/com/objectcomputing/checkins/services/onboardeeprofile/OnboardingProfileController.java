package com.objectcomputing.checkins.services.onboardeeprofile;

import
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/onboardee-profiles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "onboardee profiles")
public class OnboardingProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(OnboardingProfileController.class);
    private final OnboardingProfileServices onboardingProfileServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public OnboardingProfileController(OnboardingProfileServices onboardingProfileServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.onboardingProfileServices = onboardingProfileServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find member profile by id.
     *
     * @param id {@link UUID} ID of the member profile
     * @return {@link OnboardingProfileResponseDTO } Returned onboardee profile
     */
    @Get("/{id}")
    public Mono<HttpResponse<OnboardingProfileResponseDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> onboardingProfileServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(onboarding_Profile -> (HttpResponse<OnboardingProfileResponseDTO>) HttpResponse
                        .ok(fromEntity(onboarding_Profile))
                        .headers(headers -> headers.location(location(onboarding_Profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find onboarding profile by or find all.
     *
     * @param id {@link UUID} ID of the onboardee
     * @param firstName {@link String} Find onboardees with the given first name
     * @param middleName {@link String} Find onboardees with the given middle name
     * @param lastName {@link String} Find onboardees with the given last name
     * @param socialSecurityNumber {@link Integer} Find onboardee
     * @param birthDate  {@link LocalDate} birth date of the onboardee
     * @param currentAddress {@link String} Onboardee's current address
     * @param previousAddress {@link String} Onboardee's previous address
     * @param phoneNumber {@link Integer} Onboardee's phone number
     * @param phoneNumber {@link Integer} Onboardee's phone number
     * @return {@link List<OnboardingProfileResponseDTO>} List of Onboardees that match the input parameters
     */
    @Get("/{?firstName,lastName,title,pdlId,workEmail,supervisorId,terminated}")
    public Mono<HttpResponse<List<OnboardingProfileResponseDTO>>> findByValue(@Nullable String firstName,
                                                                          @Nullable String lastName,
                                                                          @Nullable String title,
                                                                          @Nullable UUID pdlId,
                                                                          @Nullable String workEmail,
                                                                          @Nullable UUID supervisorId,
                                                                          @QueryValue(value = "terminated" , defaultValue = "false") Boolean terminated) {
        return Mono.fromCallable(() -> onboardingProfileServices.findByValues(firstName, lastName, title, pdlId, workEmail, supervisorId, terminated))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberProfiles -> {
                    List<OnboardingProfileResponseDTO> dtoList = memberProfiles.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<OnboardingProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(scheduler);
    }

}
