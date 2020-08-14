package com.objectcomputing.checkins.services.team.member;

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
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/team/member")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "team-member")
public class TeamMemberController {

    @Inject
    private TeamMemberServices teamMemberServices;

    @Error(exception = TeamBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, TeamBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new teamMember.
     *
     * @param teamMember, {@link TeamMemberCreateDTO}
     * @return {@link HttpResponse <TeamMember>}
     */
    @Post(value = "/")
    public HttpResponse<TeamMember> createMembers(@Body @Valid TeamMemberCreateDTO teamMember,
                                                   HttpRequest<TeamMemberCreateDTO> request) {
        TeamMember newTeamMember = teamMemberServices.save(new TeamMember(teamMember.getTeamid(),
                teamMember.getMemberid(), teamMember.isLead()));
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
    public HttpResponse<?> updateMembers(@Body @Valid TeamMember teamMember, HttpRequest<TeamMember> request) {
        TeamMember updatedTeamMember = teamMemberServices.update(teamMember);
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
     * Load members
     *
     * @param teamMembers, {@link List<TeamMemberCreateDTO> to load {@link TeamMember team members}}
     * @return {@link HttpResponse<List<TeamMember>}
     */
    @Post("/members")
    public HttpResponse<?> loadTeamMembers(@Body @Valid @NotNull List<TeamMemberCreateDTO> teamMembers,
                                            HttpRequest<List<TeamMember>> request) {
        List<String> errors = new ArrayList<>();
        List<TeamMember> membersCreated = new ArrayList<>();
        for (TeamMemberCreateDTO teamMemberDTO : teamMembers) {
            TeamMember teamMember = new TeamMember(teamMemberDTO.getTeamid(),
                    teamMemberDTO.getMemberid(), teamMemberDTO.isLead());
            try {
                teamMemberServices.save(teamMember);
                membersCreated.add(teamMember);
            } catch (TeamBadArgException e) {
                errors.add(String.format("Member %s was not added to Team %s because: %s", teamMember.getMemberid(),
                        teamMember.getTeamid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(membersCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }
}
