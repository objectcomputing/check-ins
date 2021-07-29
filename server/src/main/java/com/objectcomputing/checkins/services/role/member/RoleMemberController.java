package com.objectcomputing.checkins.services.role.member;

import com.objectcomputing.checkins.services.role.member.*;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/roles/members")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "role-member")
public class RoleMemberController {

    private RoleMemberServices roleMemberServices;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public RoleMemberController(RoleMemberServices roleMemberServices,
                                 EventLoopGroup eventLoopGroup,
                                 @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.roleMemberServices = roleMemberServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
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
        RoleMember newRoleMember = roleMemberServices.save(new RoleMember(roleMember.getRoleid(),
                roleMember.getMemberid()));
        return HttpResponse
                .created(newRoleMember)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newRoleMember.getId()))));
    }

    /**
     * Update roleMember.
     *
     * @param roleMember, {@link RoleMember}
     * @return {@link HttpResponse<RoleMember>}
     */
    @Put()
    public HttpResponse<?> updateMembers(@Body @Valid RoleMemberUpdateDTO roleMember, HttpRequest<RoleMember> request) {
        RoleMember updatedRoleMember = roleMemberServices.update(new RoleMember(roleMember.getId(), roleMember.getRoleid(), roleMember.getMemberid()));
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
     * @param roleid   {@link UUID} of role
     * @param memberid {@link UUID} of member
     * @return {@link List < Role > list of roles}
     */
    @Get("/{?roleid,memberid}")
    public Set<RoleMember> findRoleMembers(@Nullable UUID roleid,
                                             @Nullable UUID memberid) {
        return roleMemberServices.findByFields(roleid, memberid);
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
