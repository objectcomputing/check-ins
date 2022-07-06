package com.objectcomputing.checkins.services.onboardeeprofile;

import io.micronaut.core.annotation.Nullable;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
     * Find member profile by id.
     *
     * @param id {@link UUID} ID of the member profile
     * @return {@link OnboardingProfileResponseDTO } Returned onboardee profile
     */
    @Get("/{id}")
    public Mono<HttpResponse<OnboardingProfileResponseDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> onboardingProfileServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(Onboarding_Profile -> (HttpResponse<OnboardingProfileResponseDTO>) HttpResponse
                        .ok(fromEntity(Onboarding_Profile))
                        .headers(headers -> headers.location(location(Onboarding_Profile.getId()))))
                .subscribeOn(scheduler);
    }

//    /**
//     * Find onboarding profile by or find all.
//     *
//     * @param id                   {@link UUID} ID of the onboardee
//     * @param firstName            {@link String} Find onboardees with the given first name
//     * @param middleName           {@link String} Find onboardees with the given middle name
//     * @param lastName             {@link String} Find onboardees with the given last name
//     * @param socialSecurityNumber {@link Integer} Find onboardee
//     * @param birthDate            {@link LocalDate} birth date of the onboardee
//     * @param currentAddress       {@link String} Onboardee's current address
//     * @param previousAddress      {@link String} Onboardee's previous address
//     * @param phoneNumber          {@link Integer} Onboardee's phone number
//     * @param phoneNumber          {@link Integer} Onboardee's phone number
//     * @return {@link List<OnboardingProfileResponseDTO>} List of Onboardees that match the input parameters
//     */
    @Get("/{?id,firstName,lastName,socialSecurityNumber,birthDate,supervisorId,terminated}")
    public Mono<HttpResponse<List<OnboardingProfileResponseDTO>>> findByValue(@Nullable UUID id,
                                                                              @Nullable String firstName,
                                                                              @Nullable String lastName,
                                                                              @Nullable Integer socialSecurityNumber,
                                                                              @Nullable LocalDate birthDate,
                                                                              @Nullable Integer phoneNumber) {
        return Mono.fromCallable(() -> onboardingProfileServices.findByValues(id, firstName, lastName, socialSecurityNumber, birthDate, phoneNumber))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(Onboarding_profile -> {
                    List<OnboardingProfileResponseDTO> dtoList = Onboarding_profile.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<OnboardingProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(scheduler);
    }


    protected URI location(UUID id) {
        return URI.create("/onboardee-profiles/" + id);
    }

    private OnboardingProfileResponseDTO fromEntity(Onboarding_Profile entity) {
        OnboardingProfileResponseDTO dto = new OnboardingProfileResponseDTO();
//        dto.setId(entity.getId());
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

//    private Onboarding_Profile fromDTO(OnboardingProfileResponseDTO dto) {
//        return new Onboarding_Profile(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
//                dto.getSocialSecurityNumber(), dto.getBirthDate(), dto.getCurrentAddress(), dto.getPreviousAddress(), dto.getPhoneNumber(),
//                dto.getSecondPhoneNumber());
//    }
//
//    private Onboarding_Profile fromDTO(OnboardingProfileCreateDTO dto) {
//        return new Onboarding_Profile(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getSocialSecurityNumber(), dto.getBirthDate(), dto.getCurrentAddress(), dto.getPreviousAddress(), dto.getPhoneNumber(),
//                dto.getSecondPhoneNumber());
//    }
}
