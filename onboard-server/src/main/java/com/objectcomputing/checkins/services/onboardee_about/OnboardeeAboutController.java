package com.objectcomputing.checkins.services.onboardee_about;

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

@Controller("/services/onboardee-about")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "onboardee about")
public class OnboardeeAboutController {
    private static final Logger LOG = LoggerFactory.getLogger(OnboardeeAbout.class);

    private final OnboardeeAboutServices onboardeeAboutServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public OnboardeeAboutController(OnboardeeAboutServices onboardeeAboutServices,
            EventLoopGroup eventLoopGroup,
            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.onboardeeAboutServices = onboardeeAboutServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<OnboardeeAboutResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> onboardeeAboutServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<OnboardeeAboutResponseDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    @Post()
    public Mono<HttpResponse<OnboardeeAboutResponseDTO>> save(@Body @Valid OnboardeeAboutCreateDTO onboardeeAbout) {
        return Mono.fromCallable(() -> onboardeeAboutServices.saveAbout(fromDTO(onboardeeAbout)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<OnboardeeAboutResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    @Put()
    public Mono<HttpResponse<OnboardeeAboutResponseDTO>> update(@Body @Valid OnboardeeAboutResponseDTO onboardeeAbout) {
        return Mono.fromCallable(() -> onboardeeAboutServices.saveAbout(fromDTO(onboardeeAbout)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    OnboardeeAboutResponseDTO updatedOnboardeeAbout = fromEntity(savedProfile);
                    return (HttpResponse<OnboardeeAboutResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedOnboardeeAbout.getId())))
                            .body(updatedOnboardeeAbout);
                })
                .subscribeOn(scheduler);
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> onboardeeAboutServices.deleteAbout(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/onboardee-about/" + id);
    }

    private OnboardeeAboutResponseDTO fromEntity(OnboardeeAbout entity) {
        OnboardeeAboutResponseDTO dto = new OnboardeeAboutResponseDTO();
        dto.setId(entity.getId());
        dto.setTshirtSize(entity.getTshirtSize());
        dto.setGoogleTraining(entity.getGoogleTraining());
        dto.setIntroduction(entity.getIntroduction());
        dto.setVaccineStatus(entity.getVaccineStatus());
        dto.setVaccineTwoWeeks(entity.getVaccineTwoWeeks());
        dto.setOtherTraining(entity.getOtherTraining());
        dto.setAdditionalSkills(entity.getAdditionalSkills());
        dto.setCertifications(entity.getCertifications());
        return dto;
    }

    private OnboardeeAbout fromDTO(OnboardeeAboutResponseDTO dto) {
        return new OnboardeeAbout(dto.getId(), dto.getTshirtSize(), dto.getGoogleTraining(), dto.getIntroduction(),
                dto.getVaccineStatus(), dto.getVaccineTwoWeeks(), dto.getOtherTraining(), dto.getAdditionalSkills(),
                dto.getCertifications());
    }

    private OnboardeeAbout fromDTO(OnboardeeAboutCreateDTO dto) {
        return new OnboardeeAbout(dto.getTshirtSize(), dto.getGoogleTraining(), dto.getIntroduction(),
                dto.getVaccineStatus(), dto.getVaccineTwoWeeks(), dto.getOtherTraining(), dto.getAdditionalSkills(),
                dto.getCertifications());
    }
}
