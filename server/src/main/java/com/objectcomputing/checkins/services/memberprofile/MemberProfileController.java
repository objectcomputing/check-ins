package com.objectcomputing.checkins.services.memberprofile;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.role.RoleBadArgException;
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

@Controller("/member-profile")
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
     * Find Team Member profile by UUID.
     * @param uuid
     * @return
     */
    @Get("/{uuid}")
    public HttpResponse<MemberProfileResponseDTO> getByUuid(UUID uuid) {

        MemberProfile result = memberProfileServices.getById(uuid);

        return HttpResponse
                .ok(fromEntity(result))
                .headers(headers -> headers.location(location(result.getUuid())));
    }

    /**
     * Find Team Member profile by Name, Role, PdlId or find all.
     * @param name
     * @param role
     * @param pdlId
     * @return
     */
    @Get("/{?name,role,pdlId}")
    public HttpResponse<List<MemberProfileResponseDTO>> findByValue(@Nullable String name, @Nullable String role, @Nullable UUID pdlId) {
        List<MemberProfileResponseDTO> responseBody = memberProfileServices.findByValues(name, role, pdlId)
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
                .headers(headers -> headers.location(location(newMemberProfile.getUuid())));
    }

    /**
     * Update a Team member profile.
     * @param memberProfile
     * @return
     */
    @Put("/")
    public HttpResponse<MemberProfileResponseDTO> update(@Body @Valid MemberProfileUpdateDTO memberProfile) {

        if(null != memberProfile.getId()) {
            MemberProfile updatedMemberProfile = memberProfileServices.saveProfile(fromDTO(memberProfile));
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedMemberProfile.getUuid())))
                    .body(fromEntity(updatedMemberProfile));

        }

        throw new MemberProfileBadArgException("Member profile id is required");
    }

    protected URI location(UUID uuid) {
        return URI.create("/member-profile/" + uuid);
    }

    private MemberProfileResponseDTO fromEntity(MemberProfile entity) {
        MemberProfileResponseDTO dto = new MemberProfileResponseDTO();
        dto.setId(entity.getUuid());
        dto.setBioText(entity.getBioText());
        dto.setInsperityId(entity.getInsperityId());
        dto.setLocation(entity.getLocation());
        dto.setName(entity.getName());
        dto.setPdlId(entity.getPdlId());
        dto.setRole(entity.getRole());
        dto.setStartDate(entity.getStartDate());
        dto.setWorkEmail(entity.getWorkEmail());
        return dto;
    }

    private MemberProfile fromDTO(MemberProfileUpdateDTO dto) {
        return new MemberProfile(dto.getName(), dto.getRole(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getInsperityId(), dto.getStartDate(),dto.getBioText());
    }

    private MemberProfile fromDTO(MemberProfileCreateDTO dto) {
        return new MemberProfile(dto.getName(), dto.getRole(), dto.getPdlId(), dto.getLocation(),
                dto.getWorkEmail(), dto.getInsperityId(), dto.getStartDate(),dto.getBioText());
    }
}
