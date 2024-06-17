package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/demographics")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "demographics")
public class DemographicsController {

    private final DemographicsServices demographicsServices;

    public DemographicsController(DemographicsServices demographicsServices) {
        this.demographicsServices = demographicsServices;
    }

    /**
     * Find demographic by id.
     *
     * @param id {@link UUID} ID of the demographic
     * @return {@link DemographicsResponseDTO} Returned demographic
     */
    @Get("/{id}")
    public HttpResponse<DemographicsResponseDTO> getById(UUID id) {
        Demographics demographic = demographicsServices.getById(id);
        return demographic == null ? null : HttpResponse.ok(fromEntity(demographic))
                .headers(headers -> headers.location(location(demographic.getId())));
    }

    /**
     * Find demographics by memberId, gender, degreeLevel, industryTenure, personOfColor, veteran, militaryTenure, militaryBranch or find all.
     *
     * @param memberId       {@link UUID} Find demographics with the given memberId
     * @param gender         {@link String} Find demographics with the given gender
     * @param degreeLevel    {@link String} Find demographics with given degree level
     * @param industryTenure {@link Integer} Find demographics with given industry tenure
     * @param personOfColor  {@link Boolean} Find demographics who are persons of color
     * @param veteran        {@link Boolean} Find demographics for are veterans
     * @param militaryTenure {@link Integer} Find demographics with given military tenure
     * @param militaryBranch {@link String} Find demographics with given military branch
     * @return {@link List <DemographicsResponseDTO>} List of demographics that match the input parameters
     */
    @Get("/{?memberId,gender,degreeLevel,industryTenure,personOfColor,veteran,militaryTenure,militaryBranch}")
    public List<DemographicsResponseDTO> findByValue(@Nullable UUID memberId,
                                                     @Nullable String gender,
                                                     @Nullable String degreeLevel,
                                                     @Nullable Integer industryTenure,
                                                     @Nullable Boolean personOfColor,
                                                     @Nullable Boolean veteran,
                                                     @Nullable Integer militaryTenure,
                                                     @Nullable String militaryBranch) {
        return demographicsServices.findByValues(memberId, gender, degreeLevel, industryTenure, personOfColor, veteran, militaryTenure, militaryBranch)
                .stream()
                .map(this::fromEntity)
                .toList();
    }

    /**
     * Save new demographics.
     *
     * @param demographics {@link DemographicsCreateDTO} Information of the demographics being created
     * @return {@link DemographicsResponseDTO} The created demographics
     */
    @Post
    public HttpResponse<DemographicsResponseDTO> save(@Body @Valid DemographicsCreateDTO demographics,
                                                      HttpRequest<?> request) {

        Demographics savedDemographic = demographicsServices.saveDemographics(fromDTO(demographics));
        return HttpResponse.created(fromEntity(savedDemographic))
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), savedDemographic.getId()))));
    }

    /**
     * Update demographics.
     *
     * @param demographics {@link DemographicsUpdateDTO} Information of the demographics being updated
     * @return {@link DemographicsResponseDTO} The updated demographics
     */
    @Put
    public HttpResponse<DemographicsResponseDTO> update(@Body @Valid DemographicsUpdateDTO demographics,
                                                        HttpRequest<?> request) {

        Demographics savedDemographics = demographicsServices.updateDemographics(fromDTO(demographics));
        return HttpResponse.ok(fromEntity(savedDemographics))
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), savedDemographics.getId()))));
    }

    /**
     * Delete demographics.
     *
     * @param id {@link UUID} Demographics unique id
     */
    @Delete("/{id}")
    @Status(value = HttpStatus.OK)
    public void delete(@NotNull UUID id) {
        demographicsServices.deleteDemographics(id);
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
