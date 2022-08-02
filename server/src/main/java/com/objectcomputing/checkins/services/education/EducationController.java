package com.objectcomputing.checkins.services.education;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
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

}
