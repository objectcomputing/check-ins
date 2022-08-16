package com.objectcomputing.checkins.services.workingenvironment;

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

@Controller("/services/working-environment")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "working environment")
public class WorkingEnvironmentController {
    private static final Logger LOG = LoggerFactory.getLogger(WorkingEnvironment.class);

    private final WorkingEnvironmentServices workingEnvironmentServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public WorkingEnvironmentController(WorkingEnvironmentServices workingEnvironmentServices,
            EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.workingEnvironmentServices = workingEnvironmentServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<WorkingEnvironmentResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> workingEnvironmentServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<WorkingEnvironmentResponseDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    @Post()
    public Mono<HttpResponse<WorkingEnvironmentResponseDTO>> save(
            @Body @Valid WorkingEnvironmentCreateDTO workingEnvironment) {
        return Mono.fromCallable(() -> workingEnvironmentServices.saveWorkingEnvironment(fromDTO(workingEnvironment)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<WorkingEnvironmentResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
    }

    @Put()
    public Mono<HttpResponse<WorkingEnvironmentResponseDTO>> update(
            @Body @Valid WorkingEnvironmentResponseDTO onboardeeAbout) {
        return Mono.fromCallable(() -> workingEnvironmentServices.saveWorkingEnvironment(fromDTO(onboardeeAbout)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    WorkingEnvironmentResponseDTO updatedWorkingEnvironment = fromEntity(savedProfile);
                    return (HttpResponse<WorkingEnvironmentResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedWorkingEnvironment.getId())))
                            .body(updatedWorkingEnvironment);
                })
                .subscribeOn(scheduler);
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> workingEnvironmentServices.deleteWorkingEnvironment(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    private URI location(UUID id) {
        return URI.create("/working-environment/" + id);
    }

    private WorkingEnvironmentResponseDTO fromEntity(WorkingEnvironment entity) {
        WorkingEnvironmentResponseDTO dto = new WorkingEnvironmentResponseDTO();
        dto.setId(entity.getId());
        dto.setWorkLocation(entity.getWorkLocation());
        dto.setKeyType(entity.getKeyType());
        dto.setOsType(entity.getOsType());
        dto.setAccessories(entity.getAccessories());
        dto.setOtherAccessories(entity.getOtherAccessories());
        return dto;
    }

    private WorkingEnvironment fromDTO(WorkingEnvironmentResponseDTO dto) {
        return new WorkingEnvironment(dto.getId(), dto.getWorkLocation(), dto.getKeyType(), dto.getOsType(),
                dto.getAccessories(), dto.getOtherAccessories());
    }

    private WorkingEnvironment fromDTO(WorkingEnvironmentCreateDTO dto) {
        return new WorkingEnvironment(dto.getWorkLocation(), dto.getKeyType(), dto.getOsType(), dto.getAccessories(),
                dto.getOtherAccessories());
    }

}
