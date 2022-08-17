package com.objectcomputing.checkins.services.onboard.background_information;

import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import io.micronaut.core.annotation.Nullable;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/background-information")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "background information")
public class BackgroundInformationController {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundInformation.class);

    private final BackgroundInformationServices backgroundInformationServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public BackgroundInformationController(BackgroundInformationServices backgroundInformationServices,
                                           EventLoopGroup eventLoopGroup,
                                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService){
        this.backgroundInformationServices = backgroundInformationServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<BackgroundInformationDTO>> getById(UUID id){
        return Mono.fromCallable(() -> backgroundInformationServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<BackgroundInformationDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    @Post()
    public Mono<HttpResponse<BackgroundInformationDTO>> save(@Body @Valid BackgroundInformationCreateDTO backgroundInformationDTO){
        return Mono.fromCallable(() -> backgroundInformationServices.saveProfile( backgroundInformationDTO))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<BackgroundInformationDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    @Put()
    public Mono<HttpResponse<BackgroundInformationDTO>> update(@Body @Valid BackgroundInformationDTO backgroundInformationDTO){
        return Mono.fromCallable(() -> backgroundInformationServices.updateProfile(backgroundInformationDTO))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    BackgroundInformationDTO updatedBackgroundInformation = fromEntity(savedProfile);
                    return (HttpResponse<BackgroundInformationDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedBackgroundInformation.getId())))
                            .body(updatedBackgroundInformation);
                })
                .subscribeOn(scheduler);
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id){
        return Mono.fromCallable(() -> backgroundInformationServices.deleteProfile(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    private URI location(UUID id) {
        return URI.create("/background-information/" + id);
    }

    private BackgroundInformationDTO fromEntity(BackgroundInformation entity) {
        BackgroundInformationDTO dto = new BackgroundInformationDTO();
        dto.setId(entity.getId());
        dto.setStepComplete(entity.getStepComplete());
        return dto;
    }

}
