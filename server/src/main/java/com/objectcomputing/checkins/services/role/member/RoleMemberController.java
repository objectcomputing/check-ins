package com.objectcomputing.checkins.services.role.member;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/roles/members")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "role-member")
public class RoleMemberController {

    private final RoleMemberServices roleMemberServices;

    public RoleMemberController(RoleMemberServices roleMemberServices) {
        this.roleMemberServices = roleMemberServices;
    }

    /**
     * Create and save a new roleMember.
     *
     * @param roleMember, {@link RoleMemberResponseDTO}
     * @return {@link HttpResponse <RoleMember>}
     */
    @Post()
    public HttpResponse<RoleMember> createMembers(@Body @Valid RoleMemberCreateDTO roleMember,
                                                  HttpRequest<RoleMemberResponseDTO> request) {
        RoleMember newRoleMember = roleMemberServices.save(new RoleMember(roleMember.getRoleId(),
                roleMember.getMemberId(), roleMember.getLead()));
        return HttpResponse
                .created(newRoleMember)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newRoleMember.getId()))));
    }

    /**
     * Update roleMember.
     *
     * @param roleMember, {@link RoleMember}
     * @return {@link HttpResponse< RoleMember >}
     */
    @Put()
    public HttpResponse<?> updateMembers(@Body @Valid RoleMemberUpdateDTO roleMember, HttpRequest<RoleMember> request) {
        RoleMember updatedRoleMember = roleMemberServices.update(new RoleMember(roleMember.getId(), roleMember.getRoleId(), roleMember.getMemberId(), roleMember.getLead()));
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedRoleMember.getId()))))
                .body(updatedRoleMember);

    }

    /**
     * Get RoleMember based off id
     *
     * @param id {@link UUID} of the role member entry
     * @return {@link RoleMember}
     */
    @Get("/{id}")
    public RoleMember readRoleMember(UUID id) {
        return roleMemberServices.read(id);
    }

    /**
     * Find role members that match all filled in parameters, return all results when given no params
     *
     * @param roleId   {@link UUID} of role
     * @param memberId {@link UUID} of member
     * @param lead,    is lead of the role
     * @return {@link List < Role > list of roles}
     */
    @Get("/{?roleId,memberId,lead}")
    public Set<RoleMember> findRoleMembers(@Nullable UUID roleId,
                                           @Nullable UUID memberId,
                                           @Nullable Boolean lead) {
        return roleMemberServices.findByFields(roleId, memberId, lead);
    }

    /**
     * Delete A RoleMember
     *
     * @param id, id of {@link UUID} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteRoleMember(@NotNull UUID id) {
        roleMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
}