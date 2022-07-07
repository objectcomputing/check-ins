package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/teams/members")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "team-member")
public class TeamMemberController {

    private final TeamMemberServices teamMemberServices;

    public TeamMemberController(TeamMemberServices teamMemberServices) {
        this.teamMemberServices = teamMemberServices;
    }

    /**
     * Create and save a new teamMember.
     *
     * @param teamMember, {@link TeamMemberResponseDTO}
     * @return {@link HttpResponse <TeamMember>}
     */
    @RequiredPermission(Permissions.CAN_CREATE_MEMBER)
    @Post()
    public HttpResponse<TeamMember> createMembers(@Body @Valid TeamMemberCreateDTO teamMember,
                                                  HttpRequest<TeamMemberResponseDTO> request) {
        TeamMember newTeamMember = teamMemberServices.save(new TeamMember(teamMember.getTeamId(),
                teamMember.getMemberId(), teamMember.getLead()));
        return HttpResponse
                .created(newTeamMember)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newTeamMember.getId()))));
    }

    /**
     * Update teamMember.
     *
     * @param teamMember, {@link TeamMember}
     * @return {@link HttpResponse<TeamMember>}
     */
    @Put()
    public HttpResponse<?> updateMembers(@Body @Valid TeamMemberUpdateDTO teamMember, HttpRequest<TeamMember> request) {
        TeamMember updatedTeamMember = teamMemberServices.update(new TeamMember(teamMember.getId(), teamMember.getTeamId(), teamMember.getMemberId(), teamMember.getLead()));
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedTeamMember.getId()))))
                .body(updatedTeamMember);

    }

    /**
     * Get TeamMember based off id
     *
     * @param id {@link UUID} of the team member entry
     * @return {@link TeamMember}
     */
    @RequiredPermission(Permissions.CAN_VIEW_MEMBER)
    @Get("/{id}")
    public TeamMember readTeamMember(UUID id) {
        return teamMemberServices.read(id);
    }

    /**
     * Find team members that match all filled in parameters, return all results when given no params
     *
     * @param teamId   {@link UUID} of team
     * @param memberId {@link UUID} of member
     * @param lead,    is lead of the team
     * @return {@link List < Team > list of teams}
     */
    @RequiredPermission(Permissions.CAN_VIEW_MEMBER)
    @Get("/{?teamId,memberId,lead}")
    public Set<TeamMember> findTeamMembers(@Nullable UUID teamId,
                                           @Nullable UUID memberId,
                                           @Nullable Boolean lead) {
        return teamMemberServices.findByFields(teamId, memberId, lead);
    }

    /**
     * Delete A TeamMember
     *
     * @param id, id of {@link UUID} to delete
     */
    @RequiredPermission(Permissions.CAN_DELETE_MEMBER)
    @Delete("/{id}")
    public HttpResponse<?> deleteTeamMember(@NotNull UUID id) {
        teamMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
}