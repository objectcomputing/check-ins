package com.objectcomputing.checkins.services.onboardeeprofile;

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
     * Find all onboardee profiles.
     *
     * @return {@link OnboardingProfileResponseDTO } Returned onboardee profiles
     */


    @Get()
    public Mono<HttpResponse<List<OnboardingProfileResponseDTO>>> findAll() {

        return Mono.fromCallable(() -> onboardingProfileServices.findAll())
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> {
                    List <OnboardingProfileResponseDTO> dtoList = profile.stream()
                            .map (this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<OnboardingProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);
                }) .subscribeOn(scheduler);
    }


    /**
     * Find onboardee profile by id.
     *
     * @param id {@link UUID} ID of the onboardee profile
     * @return {@link OnboardingProfileResponseDTO } Returned onboardee profile
     */
    @Get("/{id}")
    public Mono<HttpResponse<OnboardingProfileResponseDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> onboardingProfileServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<OnboardingProfileResponseDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find onboarding profile by or find all.
     *
     * @param id                   {@link UUID} ID of the onboardee
     * @param lastName             {@link String} Find onboardees with the given last name
     * @param socialSecurityNumber {@link Integer} Find onboardee
     * @param birthDate            {@link LocalDate} birth date of the onboardee
     * @param phoneNumber          {@link Integer} Onboardee's phone number
     * @return {@link List<OnboardingProfileResponseDTO>} List of Onboardees that match the input parameters
     */
    @Get("/{?id,firstName,lastName,socialSecurityNumber,birthDate,phoneNumber}")
    public Mono<HttpResponse<List<OnboardingProfileResponseDTO>>> findByValue(@Nullable UUID id,
                                                                              @Nullable String firstName,
                                                                              @Nullable String lastName,
                                                                              @Nullable Integer socialSecurityNumber,
                                                                              @Nullable LocalDate birthDate,
                                                                              @Nullable String phoneNumber) {
        return Mono.fromCallable(() -> onboardingProfileServices.findByValues(id, firstName, lastName, socialSecurityNumber, birthDate, phoneNumber))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(Onboarding_profile -> {
                    List<OnboardingProfileResponseDTO> dtoList = Onboarding_profile.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<OnboardingProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);
                }).subscribeOn(scheduler);
    }

    /**
     * Save a new onboardee profile.
     *
     * @param onboardeeProfile {@link OnboardingProfileCreateDTO } Information of the onboardee profile being created
     * @return {@link OnboardingProfileResponseDTO} The created onboardee profile
     */
    @Post()
    public Mono<HttpResponse<OnboardingProfileResponseDTO>> save(@Body @Valid OnboardingProfileCreateDTO onboardeeProfile) {

        return Mono.fromCallable(() -> onboardingProfileServices.saveProfile(fromDTO(onboardeeProfile)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<OnboardingProfileResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update a onboardee profile.
     *
     * @param onboardeeProfile {@link OnboardingProfileResponseDTO} Information of the onboardee profile being updated
     *                    *Note: There is no OnboardingProfileUpdateDTO since the information returned in an update is
     *                           the same as the information returned in a response
     *
     * @return {@link OnboardingProfileResponseDTO} The updated onboardee profile
     */
    @Put()
    public Mono<HttpResponse<OnboardingProfileResponseDTO>> update(@Body @Valid OnboardingProfileResponseDTO onboardeeProfile) {

        return Mono.fromCallable(() -> onboardingProfileServices.saveProfile(fromDTO(onboardeeProfile)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    OnboardingProfileResponseDTO updatedOnboardeeProfile = fromEntity(savedProfile);
                    return (HttpResponse<OnboardingProfileResponseDTO>) HttpResponse
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

    private OnboardingProfileResponseDTO fromEntity(OnboardingProfile entity) {
        OnboardingProfileResponseDTO dto = new OnboardingProfileResponseDTO();
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setSocialSecurityNumber(entity.getSocialSecurityNumber());
        dto.setBirthDate(entity.getBirthDate());
        dto.setCurrentAddress(entity.getCurrentAddress());
        dto.setPreviousAddress(entity.getPreviousAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setSecondPhoneNumber(entity.getSecondPhoneNumber());

        return dto;
    }

    private OnboardingProfile fromDTO(OnboardingProfileResponseDTO dto) {
        return new OnboardingProfile(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
                dto.getSocialSecurityNumber(), dto.getBirthDate(), dto.getCurrentAddress(), dto.getPreviousAddress(),
                dto.getPhoneNumber(), dto.getSecondPhoneNumber());
    }

    private OnboardingProfile fromDTO(OnboardingProfileCreateDTO dto) {
        return new OnboardingProfile( dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
                dto.getSocialSecurityNumber(), dto.getBirthDate(), dto.getCurrentAddress(), dto.getPreviousAddress(),
                dto.getPhoneNumber(), dto.getSecondPhoneNumber());
    }
}
