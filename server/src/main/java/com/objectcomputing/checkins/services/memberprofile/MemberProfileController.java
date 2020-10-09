package com.objectcomputing.checkins.services.memberprofile;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/services/member-profile")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="member profile")
public class MemberProfileController {

    private final MemberProfileServices memberProfileServices;

    public MemberProfileController(MemberProfileServices memberProfileServices){
        this.memberProfileServices = memberProfileServices;
    }

    @Error(exception = MemberProfileBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, MemberProfileBadArgException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest().body(error);
    }

    @Error(exception = MemberProfileDoesNotExistException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, MemberProfileDoesNotExistException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound().body(error);
    }

    /**
     * Find Team Member profile by id.
     * @param id
     * @return
     */
    @Get("/{id}")
    public HttpResponse<MemberProfileResponseDTO> getById(UUID id) {

        MemberProfile result = memberProfileServices.getById(id);

        return HttpResponse
                .ok(fromEntity(result))
                .headers(headers -> headers.location(location(result.getId())));
    }

    /**
     * Find Team Member profile by Name, title, PdlId, workEmail or find all.
     * @param name
     * @param title
     * @param pdlId
     * @param workEmail
     * @return
     */
    @Get("/{?name,title,pdlId,workEmail}")
    public HttpResponse<List<MemberProfileResponseDTO>> findByValue(@Nullable String name, @Nullable String title,
                                                                    @Nullable UUID pdlId, @Nullable String workEmail) {
        List<MemberProfileResponseDTO> responseBody = memberProfileServices.findByValues(name, title, pdlId, workEmail)
                .stream().map(memberProfile -> fromEntity(memberProfile)).collect(Collectors.toList());
        return HttpResponse
                .ok(responseBody);
    }

    /**
     * Save a new team member profile.
     * @param memberProfile
     * @return
     */
    @Post("/")
    public HttpResponse<MemberProfileResponseDTO> save(@Body @Valid MemberProfileCreateDTO memberProfile) {
        MemberProfile newMemberProfile = memberProfileServices.saveProfile(fromDTO(memberProfile));

        return HttpResponse
                .created(fromEntity(newMemberProfile))
                .headers(headers -> headers.location(location(newMemberProfile.getId())));
    }

    /**
     * Update a Team member profile.
     * @param memberProfile
     * @return
     */
    @Put("/")
    public HttpResponse<MemberProfileResponseDTO> update(@Body @Valid MemberProfileUpdateDTO memberProfile) {
        MemberProfile updatedMemberProfile = memberProfileServices.saveProfile(fromDTO(memberProfile));
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(updatedMemberProfile.getId())))
                .body(fromEntity(updatedMemberProfile));
    }

    protected URI location(UUID id) {
        return URI.create("/member-profile/" + id);
    }

    private MemberProfileResponseDTO fromEntity(MemberProfile entity) {
        MemberProfileResponseDTO dto = new MemberProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setBioText(entity.getBioText());
        dto.setInsperityId(entity.getInsperityId());
        dto.setLocation(entity.getLocation());
        dto.setName(entity.getName());
        dto.setPdlId(entity.getPdlId());
        dto.setTitle(entity.getTitle());
        dto.setStartDate(entity.getStartDate());
        dto.setWorkEmail(entity.getWorkEmail());
        return dto;
    }

    private MemberProfile fromDTO(MemberProfileUpdateDTO dto) {
        return new MemberProfile(dto.getId(), dto.getName(), dto.getTitle(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getInsperityId(), dto.getStartDate(),dto.getBioText());
    }

    private MemberProfile fromDTO(MemberProfileCreateDTO dto) {
        return new MemberProfile(dto.getName(), dto.getTitle(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getInsperityId(), dto.getStartDate(),dto.getBioText());
    }
}
