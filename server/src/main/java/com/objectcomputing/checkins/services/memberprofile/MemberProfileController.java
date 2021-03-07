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

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/member-profile")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "member profile")
public class MemberProfileController {

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
     * Find Team Member profile by id.
     *
     * @param id
     * @return
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
     * Find Team Member profile by Name, title, PdlId, workEmail, SupervisorId or find all.
     *
     * @param name
     * @param title
     * @param pdlId
     * @param workEmail
     * @param supervisorId
     * @return
     */
    @Get("/{?name,title,pdlId,workEmail,supervisorId}")
    public Single<HttpResponse<List<MemberProfileResponseDTO>>> findByValue(@Nullable String name,
                                                                            @Nullable String title,
                                                                            @Nullable UUID pdlId,
                                                                            @Nullable String workEmail,
                                                                            @Nullable UUID supervisorId) {
        return Single.fromCallable(() -> memberProfileServices.findByValues(name, title, pdlId, workEmail, supervisorId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(memberProfiles -> {
                    List<MemberProfileResponseDTO> dtoList = memberProfiles.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<MemberProfileResponseDTO>>) HttpResponse
                            .ok(dtoList);

                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Save a new team member profile.
     *
     * @param memberProfile
     * @return
     */
    @Post()
    public Single<HttpResponse<MemberProfileResponseDTO>> save(@Body @Valid MemberProfileCreateDTO memberProfile) {

        return Single.fromCallable(() -> memberProfileServices.saveProfile(fromDTO(memberProfile)))
                .observeOn(Schedulers.from(eventLoopGroup)).map(savedProfile -> (HttpResponse<MemberProfileResponseDTO>) HttpResponse
                        .created(fromEntity(savedProfile))
                        .headers(headers -> headers.location(location(savedProfile.getId())))).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a Team member profile.
     *
     * @param memberProfile
     * @return
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
     * @param id member unique id
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
        return URI.create("/member-profile/" + id);
    }

    private MemberProfileResponseDTO fromEntity(MemberProfile entity) {
        MemberProfileResponseDTO dto = new MemberProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTitle(entity.getTitle());
        dto.setPdlId(entity.getPdlId());
        dto.setLocation(entity.getLocation());
        dto.setWorkEmail(entity.getWorkEmail());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setStartDate(entity.getStartDate());
        dto.setBioText(entity.getBioText());
        dto.setSupervisorid(entity.getSupervisorid());
        dto.setTerminationDate(entity.getTerminationDate());
        return dto;
    }

    private MemberProfile fromDTO(MemberProfileUpdateDTO dto) {
        return new MemberProfile(dto.getId(), dto.getName(), dto.getTitle(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getEmployeeId(), dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(), dto.getTerminationDate());
    }

    private MemberProfile fromDTO(MemberProfileCreateDTO dto) {
        return new MemberProfile(dto.getName(), dto.getTitle(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getEmployeeId(), dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(), dto.getTerminationDate());
    }
}
