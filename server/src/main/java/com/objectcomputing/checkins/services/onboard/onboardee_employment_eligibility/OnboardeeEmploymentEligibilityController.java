package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

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

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/onboardee-employment-eligibility")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "onboardee employment eligibility")
public class OnboardeeEmploymentEligibilityController {
    private static final Logger LOG = LoggerFactory.getLogger(OnboardeeEmploymentEligibilityController.class);

    private final OnboardeeEmploymentEligibilityServices onboardeeEmploymentEligibilityServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public OnboardeeEmploymentEligibilityController(OnboardeeEmploymentEligibilityServices onboardeeEmploymentEligibilityServices,
                                                    EventLoopGroup eventLoopGroup,
                                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.onboardeeEmploymentEligibilityServices = onboardeeEmploymentEligibilityServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> onboardeeEmploymentEligibilityServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    @Get("/{?id,ageLegal,usCitizen,visaStatus,expirationDate,felonyStatus,felonyExplanation}")
    public Mono<HttpResponse<List<OnboardeeEmploymentEligibilityResponseDTO>>> findByValue(@Nullable UUID id,
                                                                                           @Nullable Boolean ageLegal,
                                                                                           @Nullable Boolean usCitizen,
                                                                                           @Nullable String visaStatus,
                                                                                           @Nullable LocalDate expirationDate,
                                                                                           @Nullable Boolean felonyStatus,
                                                                                           @Nullable String felonyExplanation) {
        return Mono.fromCallable(() -> onboardeeEmploymentEligibilityServices.findByValues(id, ageLegal, usCitizen, visaStatus, expirationDate, felonyStatus, felonyExplanation))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(OnboardeeEmploymentEligibility -> {
                    List<OnboardeeEmploymentEligibilityResponseDTO> dtoList = OnboardeeEmploymentEligibility.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<OnboardeeEmploymentEligibilityResponseDTO>>) HttpResponse
                            .ok(dtoList);
                }).subscribeOn(scheduler);
    }

    @Post()
    public Mono<HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>> save(@Body @Valid OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibility) {
        return Mono.fromCallable(() -> onboardeeEmploymentEligibilityServices.saveProfile(fromDTO(onboardeeEmploymentEligibility)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    @Put()
    public Mono<HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>> update(@Body @Valid OnboardeeEmploymentEligibilityResponseDTO onboardeeEmploymentEligibility) {
        LOG.info(":)");
        return Mono.fromCallable(() -> onboardeeEmploymentEligibilityServices.saveProfile(fromDTO(onboardeeEmploymentEligibility)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    OnboardeeEmploymentEligibilityResponseDTO updatedOnboardeeEmploymentEligibility = fromEntity(savedProfile);
                    return (HttpResponse<OnboardeeEmploymentEligibilityResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedOnboardeeEmploymentEligibility.getId())))
                            .body(updatedOnboardeeEmploymentEligibility);
                })
                .subscribeOn(scheduler);
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> onboardeeEmploymentEligibilityServices.deleteProfile(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/onboardee-employment-eligibility/" + id);
    }

    private OnboardeeEmploymentEligibilityResponseDTO fromEntity(OnboardeeEmploymentEligibility entity) {
        OnboardeeEmploymentEligibilityResponseDTO dto = new OnboardeeEmploymentEligibilityResponseDTO();
        dto.setId(entity.getId());
        dto.setAgeLegal(entity.getAgeLegal());
        dto.setUsCitizen(entity.getUsCitizen());
        dto.setVisaStatus(entity.getVisaStatus());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setFelonyStatus(entity.getFelonyStatus());
        dto.setFelonyExplanation(entity.getFelonyExplanation());
        return dto;
    }

    private OnboardeeEmploymentEligibility fromDTO(OnboardeeEmploymentEligibilityResponseDTO dto) {
        return new OnboardeeEmploymentEligibility(dto.getId(), dto.getAgeLegal(), dto.getUsCitizen(), dto.getVisaStatus(),
                dto.getExpirationDate(), dto.getFelonyStatus(), dto.getFelonyExplanation());
    }

    private OnboardeeEmploymentEligibility fromDTO(OnboardeeEmploymentEligibilityCreateDTO dto) {
        return new OnboardeeEmploymentEligibility(dto.getAgeLegal(), dto.getUsCitizen(), dto.getVisaStatus(),
                dto.getExpirationDate(), dto.getFelonyStatus(), dto.getFelonyExplanation());
    }
}