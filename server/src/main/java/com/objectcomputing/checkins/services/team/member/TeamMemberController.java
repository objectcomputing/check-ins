package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.team.TeamBadArgException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/team/member")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "team-member")
public class TeamMemberController {

    private final TeamMemberServices teamMemberServices;

    public TeamMemberController(TeamMemberServices teamMemberServices) {
        this.teamMemberServices = teamMemberServices;
    }

    @Error(exception = TeamBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, TeamBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = BadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, BadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new teamMember.
     *
     * @param teamMember, {@link TeamMemberResponseDTO}
     * @return {@link HttpResponse <TeamMember>}
     */
    @Post(value = "/")
    public HttpResponse<TeamMember> createMembers(@Body @Valid TeamMemberCreateDTO teamMember,
                                                  HttpRequest<TeamMemberResponseDTO> request) {
        TeamMember newTeamMember = teamMemberServices.save(new TeamMember(teamMember.getTeamid(),
                teamMember.getMemberid(), teamMember.getLead()));
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
    @Put("/")
    public HttpResponse<?> updateMembers(@Body @Valid TeamMemberUpdateDTO teamMember, HttpRequest<TeamMember> request) {
        TeamMember updatedTeamMember = teamMemberServices.update(new TeamMember(teamMember.getId(), teamMember.getTeamid(), teamMember.getMemberid(), teamMember.getLead()));
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
    @Get("/{id}")
    public TeamMember readTeamMember(UUID id) {
        return teamMemberServices.read(id);
    }

    /**
     * Find team members that match all filled in parameters, return all results when given no params
     *
     * @param teamid  {@link UUID} of team
     * @param memberid {@link UUID} of member
     * @param lead,    is lead of the team
     * @return {@link List < Team > list of teams}
     */
    @Get("/{?teamid,memberid,lead}")
    public Set<TeamMember> findTeamMembers(@Nullable UUID teamid,
                                           @Nullable UUID memberid,
                                           @Nullable Boolean lead) {
        return teamMemberServices.findByFields(teamid, memberid, lead);
    }

    /**
     * Delete A TeamMember
     *
     * @param id, id of {@link TeamMember} to delete
     */
    @Delete("/{id}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> deleteSkill(@NotNull UUID id) {
        teamMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
}