package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    private final ExecutorService ioExecutorService;

    public MemberProfileController(MemberProfileServices memberProfileServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberProfileServices = memberProfileServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find member profile by id.
     *
     * @param id {@link UUID} ID of the member profile
     * @return {@link MemberProfileResponseDTO} Returned member profile
     */
    @Get("/{id}")
    public Single<HttpResponse<MemberProfileResponseDTO>> getById(UUID id) {

        return Single.fromCallable(() -> memberProfileServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(memberProfile -> (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                        .ok(fromEntity(memberProfile))
                        .headers(headers -> headers.location(location(memberProfile.getId()))))
                .subscribeOn(Schedulers.from(ioExecutorService));
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
    public Single<HttpResponse<List<MemberProfileResponseDTO>>> findByValue(@Nullable String firstName,
                                                                            @Nullable String lastName,
                                                                            @Nullable String title,
                                                                            @Nullable UUID pdlId,
                                                                            @Nullable String workEmail,
                                                                            @Nullable UUID supervisorId,
                                                                            @QueryValue(value = "terminated" , defaultValue = "false") Boolean terminated) {
        return Single.fromCallable(() -> memberProfileServices.findByValues(firstName, lastName, title, pdlId, workEmail, supervisorId, terminated))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(memberProfiles -> {
                    List<MemberProfileResponseDTO> dtoList = memberProfiles.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<MemberProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Save a new member profile.
     *
     * @param memberProfile {@link MemberProfileCreateDTO} Information of the member profile being created
     * @return {@link MemberProfileResponseDTO} The created member profile
     */
    @Post()
    public Single<HttpResponse<MemberProfileResponseDTO>> save(@Body @Valid MemberProfileCreateDTO memberProfile) {

        return Single.fromCallable(() -> memberProfileServices.saveProfile(fromDTO(memberProfile)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedProfile -> (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId()))))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a member profile.
     *
     * @param memberProfile {@link MemberProfileUpdateDTO} Information of the member profile being updated
     * @return {@link MemberProfileResponseDTO} The updated member profile
     */
    @Put()
    public Single<HttpResponse<MemberProfileResponseDTO>> update(@Body @Valid MemberProfileUpdateDTO memberProfile) {

        return Single.fromCallable(() -> memberProfileServices.saveProfile(fromDTO(memberProfile)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedProfile -> {
                    MemberProfileResponseDTO updatedMemberProfile = fromEntity(savedProfile);
                    return (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedMemberProfile.getId())))
                            .body(updatedMemberProfile);
                })
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete a member profile
     *
     * @param id {@link UUID} Member unique id
     * @return
     */
    @Delete("/{id}")
    public Single<HttpResponse> delete(@NotNull UUID id) {
        return Single.fromCallable(() -> memberProfileServices.deleteProfile(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));
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
