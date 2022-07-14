package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
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

@Controller("/services/demographics")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "demographics")
public class DemographicsController {

    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;
    private final DemographicsServices demographicsServices;

    public DemographicsController(@Named(TaskExecutors.IO) ExecutorService executorService, EventLoopGroup eventLoopGroup, DemographicsServices demographicsServices) {
        this.scheduler = Schedulers.fromExecutorService(executorService);
        this.eventLoopGroup = eventLoopGroup;
        this.demographicsServices = demographicsServices;
    }

    /**
     * Find demographic by id.
     *
     * @param id {@link UUID} ID of the demographic
     * @return {@link DemographicsResponseDTO} Returned demographic
     */
    @Get("/{id}")
    public Mono<HttpResponse<DemographicsResponseDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> demographicsServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(demographic -> (HttpResponse<DemographicsResponseDTO>) HttpResponse
                        .ok(fromEntity(demographic))
                        .headers(headers -> headers.location(location(demographic.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find demographics by memberId, gender, degreeLevel, industryTenure, personOfColor, veteran, militaryTenure, militaryBranch or find all.
     *
     * @param memberId {@link UUID} Find demographics with the given memberId
     * @param gender {@link String} Find demographics with the given gender
     * @param degreeLevel {@link String} Find demographics with given degree level
     * @param industryTenure {@link Integer} Find demographics with given industry tenure
     * @param personOfColor {@link Boolean} Find demographics who are persons of color
     * @param veteran {@link Boolean} Find demographics for are veterans
     * @param militaryTenure {@link Integer} Find demographics with given military tenure
     * @param militaryBranch {@link String} Find demographics with given military branch
     * @return {@link List <DemographicsResponseDTO>} List of demographics that match the input parameters
     */
    @Get("/{?memberId,gender,degreeLevel,industryTenure,personOfColor,veteran,militaryTenure,militaryBranch}")
    public Mono<HttpResponse<List<DemographicsResponseDTO>>> findByValue(@Nullable UUID memberId,
                                                                            @Nullable String gender,
                                                                            @Nullable String degreeLevel,
                                                                            @Nullable Integer industryTenure,
                                                                            @Nullable Boolean personOfColor,
                                                                            @Nullable Boolean veteran,
                                                                            @Nullable Integer militaryTenure,
                                                                            @Nullable String militaryBranch) {
        return Mono.fromCallable(() -> demographicsServices.findByValues(memberId, gender, degreeLevel, industryTenure, personOfColor, veteran, militaryTenure, militaryBranch))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(demographics -> {
                    List<DemographicsResponseDTO> dtoList = demographics.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<DemographicsResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(scheduler);
    }

    /**
     * Save new demographics.
     *
     * @param demographics {@link DemographicsCreateDTO} Information of the demographics being created
     * @return {@link DemographicsResponseDTO} The created demographics
     */
    @Post()
    public Mono<HttpResponse<DemographicsResponseDTO>> save(@Body @Valid DemographicsCreateDTO demographics,
                                                              HttpRequest<DemographicsCreateDTO> request) {

        return Mono.fromCallable(() -> demographicsServices.saveDemographics(fromDTO(demographics)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedDemographics -> {
                        DemographicsResponseDTO savedDemographicsResponse = fromEntity(savedDemographics);
                        return (HttpResponse<DemographicsResponseDTO>) HttpResponse
                                .created(savedDemographicsResponse)
                                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), savedDemographicsResponse.getId()))))
                                .body(savedDemographicsResponse);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Update demographics.
     *
     * @param demographics {@link DemographicsUpdateDTO} Information of the demographics being updated
     * @return {@link DemographicsResponseDTO} The updated demographics
     */
    @Put()
    public Mono<HttpResponse<DemographicsResponseDTO>> update(@Body @Valid DemographicsUpdateDTO demographics,
                                                                HttpRequest<DemographicsUpdateDTO> request) {

        return Mono.fromCallable(() -> demographicsServices.updateDemographics(fromDTO(demographics)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedDemographics -> {
                    DemographicsResponseDTO updatedDemographics = fromEntity(savedDemographics);
                    return (HttpResponse<DemographicsResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedDemographics.getId()))))
                            .body(updatedDemographics);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete demographics.
     *
     * @param id {@link UUID} Demographics unique id
     * @return
     */
    @Delete("/{id}")
    public Mono<HttpResponse> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> demographicsServices.deleteDemographics(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) {
        return URI.create("/demographics/" + id);
    }

    private Demographics fromDTO(DemographicsCreateDTO dto) {
        return new Demographics(dto.getMemberId(), dto.getGender(), dto.getDegreeLevel(),
                dto.getIndustryTenure(), dto.getPersonOfColor(), dto.getVeteran(),
                dto.getMilitaryTenure(), dto.getMilitaryBranch());
    }

    private Demographics fromDTO(DemographicsUpdateDTO dto) {
        return new Demographics(dto.getId(), dto.getMemberId(), dto.getGender(), dto.getDegreeLevel(),
                dto.getIndustryTenure(), dto.getPersonOfColor(), dto.getVeteran(),
                dto.getMilitaryTenure(), dto.getMilitaryBranch());
    }

    private DemographicsResponseDTO fromEntity(Demographics entity) {
        DemographicsResponseDTO dto = new DemographicsResponseDTO();
        dto.setId(entity.getId());
        dto.setMilitaryBranch(entity.getMilitaryBranch());
        dto.setDegreeLevel(entity.getDegreeLevel());
        dto.setGender(entity.getGender());
        dto.setIndustryTenure(entity.getIndustryTenure());
        dto.setMemberId(entity.getMemberId());
        dto.setMilitaryTenure(entity.getMilitaryTenure());
        dto.setPersonOfColor(entity.getPersonOfColor());
        dto.setVeteran(entity.getVeteran());
        return dto;
    }

}
