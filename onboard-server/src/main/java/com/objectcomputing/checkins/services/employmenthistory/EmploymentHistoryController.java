package com.objectcomputing.checkins.services.employmenthistory;

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

@Controller("/services/employment-history")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "employment history")
public class EmploymentHistoryController {
    private final EmploymentHistoryServices employmentHistoryServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public EmploymentHistoryController(EmploymentHistoryServices employmentHistoryServices,
            EventLoopGroup eventLoopGroup,
            @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.employmentHistoryServices = employmentHistoryServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get("/{id}")
    public Mono<HttpResponse<EmploymentHistoryDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> employmentHistoryServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<EmploymentHistoryDTO>) HttpResponse.ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    @Get("/{?id,company,companyAddress,jobTitle,startDate,endDate,reason}")
    public Mono<HttpResponse<List<EmploymentHistoryDTO>>> findByValue(@Nullable UUID id,
            @Nullable String company,
            @Nullable String companyAddress,
            @Nullable String jobTitle,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String reason) {
        return Mono.fromCallable(() -> employmentHistoryServices.findByValues(id, company, companyAddress, jobTitle,
                startDate, endDate, reason))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(Employmenthistory -> {
                    List<EmploymentHistoryDTO> dtoList = Employmenthistory.stream().map(this::fromEntity)
                            .collect(Collectors.toList());
                    return (HttpResponse<List<EmploymentHistoryDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/employment-history/" + id);
    }

    private EmploymentHistoryDTO fromEntity(EmploymentHistory entity) {
        EmploymentHistoryDTO dto = new EmploymentHistoryDTO();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setCompanyAddress(entity.getCompanyAddress());
        dto.setJobTitle(entity.getJobTitle());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setReason(entity.getReason());
        return dto;
    }

    private EmploymentHistory fromDTO(EmploymentHistoryDTO dto) {
        return new EmploymentHistory(dto.getId(), dto.getCompany(), dto.getCompanyAddress(), dto.getJobTitle(),
                dto.getStartDate(), dto.getEndDate(), dto.getReason());
    }

    private EmploymentHistory fromDTO(EmploymentHistoryCreateDTO dto) {
        return new EmploymentHistory(dto.getCompany(), dto.getCompanyAddress(), dto.getJobTitle(),
                dto.getStartDate(), dto.getEndDate(), dto.getReason());
    }
}
