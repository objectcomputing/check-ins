package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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
     * Find onboardee profile by id.
     *
     * @param id {@link UUID} ID of the onboardee profile
     * @return {@link OnboardingProfileDTO } Returned onboardee profile
     */
    @Get("/{id}")
    public Mono<HttpResponse<OnboardingProfileDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> onboardingProfileServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<OnboardingProfileDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Save a new onboardee profile.
     *
     * @param onboardeeProfile {@link OnboardingProfileCreateDTO } Information of the onboardee profile being created
     * @return {@link OnboardingProfileDTO} The created onboardee profile
     */
    @Post()
    public Mono<HttpResponse<OnboardingProfileDTO>> save(@Body @Valid OnboardingProfileCreateDTO onboardeeProfile) {

        return Mono.fromCallable(() -> onboardingProfileServices.saveProfile(onboardeeProfile))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<OnboardingProfileDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update a onboardee profile.
     *
     * @param onboardeeProfile {@link OnboardingProfileDTO} Information of the onboardee profile being updated
     *                    *Note: There is no OnboardingProfileUpdateDTO since the information returned in an update is
     *                           the same as the information returned in a response
     *
     * @return {@link OnboardingProfileDTO} The updated onboardee profile
     */
    @Put()
    public Mono<HttpResponse<OnboardingProfileDTO>> update(@Body @Valid OnboardingProfileDTO onboardeeProfile) {
        LOG.info(":)");
        return Mono.fromCallable(() -> onboardingProfileServices.updateProfile(onboardeeProfile))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    OnboardingProfileDTO updatedOnboardeeProfile = fromEntity(savedProfile);
                    return (HttpResponse<OnboardingProfileDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedOnboardeeProfile.getId())))
                            .body(updatedOnboardeeProfile);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete a onboardee profile
     *
     * @param id {@link UUID} Member unique id
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> onboardingProfileServices.deleteProfile(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/onboardee-profiles/" + id);
    }
    private OnboardingProfileDTO fromEntity(OnboardingProfile entity) {
        OnboardingProfileDTO dto = new OnboardingProfileDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setSocialSecurityNumber(entity.getSocialSecurityNumber());
        dto.setBirthDate(entity.getBirthDate());
        dto.setCurrentAddress(entity.getCurrentAddress());
        dto.setPreviousAddress(entity.getPreviousAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setSecondPhoneNumber(entity.getSecondPhoneNumber());
        dto.setPersonalEmail(entity.getPersonalEmail());
        return dto;
    }
}
