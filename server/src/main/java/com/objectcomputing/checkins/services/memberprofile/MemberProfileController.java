package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/member-profiles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "member profiles")
public class MemberProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(MemberProfileController.class);
    private final MemberProfileServices memberProfileServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public MemberProfileController(MemberProfileServices memberProfileServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberProfileServices = memberProfileServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberProfile -> (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                        .ok(fromEntity(memberProfile))
                        .headers(headers -> headers.location(location(memberProfile.getId()))))
                .subscribeOn(scheduler);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberProfiles -> {
                    List<MemberProfileResponseDTO> dtoList = memberProfiles.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<MemberProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(scheduler);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(memberProfiles -> {
                    List<MemberProfileResponseDTO> dtoList = memberProfiles.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<MemberProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(scheduler);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(scheduler);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedProfile -> {
                    MemberProfileResponseDTO updatedMemberProfile = fromEntity(savedProfile);
                    return (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedMemberProfile.getId())))
                            .body(updatedMemberProfile);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete a member profile
     *
     * @param id {@link UUID} Member unique id
     * @return
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_DELETE_ORGANIZATION_MEMBERS)
    public Mono<HttpResponse> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> memberProfileServices.deleteProfile(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(scheduler);
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
