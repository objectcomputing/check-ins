package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/member-profiles")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "member profiles")
public class MemberProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(MemberProfileController.class);
    private final MemberProfileServices memberProfileServices;

    public MemberProfileController(MemberProfileServices memberProfileServices) {
        this.memberProfileServices = memberProfileServices;
    }

    /**
     * Find member profile by id.
     *
     * @param id {@link UUID} ID of the member profile
     * @return {@link MemberProfileResponseDTO} Returned member profile
     */
    @Get("/{id}")
    public Mono<HttpResponse<MemberProfileResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> memberProfileServices.getById(id))
                .map(memberProfile -> HttpResponse.ok(fromEntity(memberProfile))
                        .headers(headers -> headers.location(location(memberProfile.getId()))));
    }

    /**
     * Find supervisors by member profile id.
     *
     * @param id {@link UUID} ID of the member profile
     * @return {@link List<MemberProfileResponseDTO>} List of the profiles for the supervisors of the requested member
     */
    @Get("/{id}/supervisors")
    public Mono<HttpResponse<List<MemberProfileResponseDTO>>> getSupervisorsForId(UUID id) {
        return Mono.fromCallable(() -> memberProfileServices.getSupervisorsForId(id))
                .map(entities -> entities.stream()
                        .map(this::fromEntity).collect(Collectors.toList()))
                .map(HttpResponse::ok);
    }

    /**
     * Find member profile by first name, last name, title, leader's ID, email, supervisor's ID or find all.
     *
     * @param firstName {@link String} Find members with the given first name
     * @param lastName {@link String} Find member with the given last name
     * @param title {@link String} Find member
     * @param pdlId {@link UUID} ID of the leader
     * @param workEmail {@link String} Requested work email
     * @param supervisorId {@link UUID} ID of the supervisor
     * @return {@link List<MemberProfileResponseDTO>} List of members that match the input parameters
     */
    @Get("/{?firstName,lastName,title,pdlId,workEmail,supervisorId,terminated}")
    public Mono<HttpResponse<List<MemberProfileResponseDTO>>> findByValue(@Nullable String firstName,
                                                                            @Nullable String lastName,
                                                                            @Nullable String title,
                                                                            @Nullable UUID pdlId,
                                                                            @Nullable String workEmail,
                                                                            @Nullable UUID supervisorId,
                                                                            @QueryValue(value = "terminated" , defaultValue = "false") Boolean terminated) {
        return Mono.fromCallable(() -> memberProfileServices.findByValues(firstName, lastName, title, pdlId, workEmail, supervisorId, terminated))
                .map(entities -> entities.stream().map(this::fromEntity).collect(Collectors.toList()))
                .map(HttpResponse::ok);
    }

    /**
     * Save a new member profile.
     *
     * @param memberProfile {@link MemberProfileCreateDTO} Information of the member profile being created
     * @return {@link MemberProfileResponseDTO} The created member profile
     */
    @Post()
    @RequiredPermission(Permission.CAN_CREATE_ORGANIZATION_MEMBERS)
    public Mono<HttpResponse<MemberProfileResponseDTO>> save(@Body @Valid MemberProfileCreateDTO memberProfile) {

        return Mono.fromCallable(() -> memberProfileServices.saveProfile(fromDTO(memberProfile)))
                .map(savedProfile -> HttpResponse.created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))));
    }

    /**
     * Update a member profile.
     *
     * @param memberProfile {@link MemberProfileUpdateDTO} Information of the member profile being updated
     * @return {@link MemberProfileResponseDTO} The updated member profile
     */
    @Put()
    public Mono<HttpResponse<MemberProfileResponseDTO>> update(@Body @Valid MemberProfileUpdateDTO memberProfile) {

        return Mono.fromCallable(() -> memberProfileServices.saveProfile(fromDTO(memberProfile)))
                .map(savedProfile -> {
                    MemberProfileResponseDTO updatedMemberProfile = fromEntity(savedProfile);
                    return HttpResponse.ok(updatedMemberProfile)
                            .headers(headers -> headers.location(location(updatedMemberProfile.getId())));
                });
    }

    /**
     * Delete a member profile
     *
     * @param id {@link UUID} Member unique id
     * @return
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_DELETE_ORGANIZATION_MEMBERS)
    public Mono<HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> memberProfileServices.deleteProfile(id))
                .map(successFlag -> HttpResponse.ok());
    }

    protected URI location(UUID id) {
        return URI.create("/member-profiles/" + id);
    }

    private MemberProfileResponseDTO fromEntity(MemberProfile entity) {
        MemberProfileResponseDTO dto = new MemberProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setSuffix(entity.getSuffix());
        dto.setName(MemberProfileUtils.getFullName(entity));
        dto.setTitle(entity.getTitle());
        dto.setPdlId(entity.getPdlId());
        dto.setLocation(entity.getLocation());
        dto.setWorkEmail(entity.getWorkEmail());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setStartDate(entity.getStartDate());
        dto.setBioText(entity.getBioText());
        dto.setSupervisorid(entity.getSupervisorid());
        dto.setTerminationDate(entity.getTerminationDate());
        dto.setBirthDay(entity.getBirthDate());
        return dto;
    }

    private MemberProfile fromDTO(MemberProfileUpdateDTO dto) {
        return new MemberProfile(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
                dto.getSuffix(), dto.getTitle(), dto.getPdlId(), dto.getLocation(), dto.getWorkEmail(),
                dto.getEmployeeId(), dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(),
                dto.getTerminationDate(),dto.getBirthDay(), dto.getVoluntary(), dto.getExcluded());
    }

    private MemberProfile fromDTO(MemberProfileCreateDTO dto) {
        return new MemberProfile(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getSuffix(),
                dto.getTitle(), dto.getPdlId(), dto.getLocation(), dto.getWorkEmail(), dto.getEmployeeId(),
                dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(), dto.getTerminationDate(), dto.getBirthDay(),
                dto.getVoluntary(), dto.getExcluded());
    }
}
