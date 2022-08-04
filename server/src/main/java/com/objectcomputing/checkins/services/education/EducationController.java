package com.objectcomputing.checkins.services.education;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.MediaType;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Controller("/services/education")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "education")
public class EducationController {
    private final EducationServices educationServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public EducationController(EducationServices educationServices, EventLoopGroup eventLoopGroup,
            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.educationServices = educationServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<EducationDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> educationServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<EducationDTO>) HttpResponse.ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }
    
    @Post()
    public Mono<HttpResponse<EducationDTO>> save(@Body @Valid EducationCreateDTO education) {

        return Mono.fromCallable(() -> educationServices.saveEducation(fromDTO(education)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedEducation -> (HttpResponse<EducationDTO>) HttpResponse
                        .created(fromEntity(savedEducation))
                        .headers(headers -> headers.location(location(savedEducation.getId()))))
                .subscribeOn(scheduler);
    }

    @Put()
    public Mono<HttpResponse<EducationDTO>> update(@Body @Valid EducationDTO education) {
        return Mono.fromCallable(() -> educationServices.saveEducation(fromDTO(education)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    EducationDTO updatedEducation = fromEntity(savedProfile);
                    return (HttpResponse<EducationDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedEducation.getId())))
                            .body(updatedEducation);
                })
                .subscribeOn(scheduler);
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> educationServices.deleteEducation(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/education/" + id);
    }

    private EducationDTO fromEntity(Education entity) {
        EducationDTO dto = new EducationDTO();
        dto.setId(entity.getId());
        dto.setHighestDegree(entity.getHighestDegree());
        dto.setInstitution(entity.getInstitution());
        dto.setLocation(entity.getLocation());
        dto.setDegree(entity.getDegree());
        dto.setCompletionDate(entity.getCompletionDate());
        dto.setMajor(entity.getMajor());
        dto.setAdditionalInfo(entity.getAdditionalInfo());
        return dto;
    }

    private Education fromDTO(EducationDTO dto) {
        return new Education(dto.getId(), dto.getHighestDegree(), dto.getInstitution(), dto. getLocation(), 
                            dto.getDegree(), dto.getCompletionDate(), dto.getMajor(), dto.getAdditionalInfo());
    }

    private Education fromDTO(EducationCreateDTO dto) {
        return new Education(dto.getHighestDegree(), dto.getInstitution(), dto. getLocation(), 
                            dto.getDegree(), dto.getCompletionDate(), dto.getMajor(), dto.getAdditionalInfo());
    }

}
