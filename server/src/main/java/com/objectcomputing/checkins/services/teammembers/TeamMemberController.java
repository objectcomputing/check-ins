package com.objectcomputing.checkins.services.teammembers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/services/team-member")
@Secured(SecurityRule.IS_ANONYMOUS)
// @Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="team-member")
public class TeamMemberController {

    protected final TeamMemberRepository teamMemberRepository;

    public TeamMemberController(TeamMemberRepository teamMemberRepository){
        this.teamMemberRepository = teamMemberRepository;
    }
    /**
     * Find Team member by team, member or find all.
     * @param teamId
     * @param memberId
     * @return
     */
    @Get("/{?teamId,memberId}")
    public List<TeamMember> findByValue(@Nullable UUID teamId, @Nullable UUID memberId) {

        if(teamId != null) {
            return teamMemberRepository.findByTeamId(teamId);
        } else if(memberId != null) {
            return teamMemberRepository.findByMemberId(memberId);
        } else {
            return teamMemberRepository.findAll();
        }
    }

    /**
     * Save a new team member.
     * @param teamMember
     * @return
     */
    @Post("/")
    // @Secured("VIEW")

    public HttpResponse<TeamMember> save(@Body @Valid TeamMember teamMember) {
        TeamMember newTeamMember = teamMemberRepository.save(teamMember);
        
        return HttpResponse
                .created(newTeamMember)
                .headers(headers -> headers.location(location(newTeamMember.getUuid())));
    }

    /**
     * Update a Team member.
     * @param teamMember
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid TeamMember teamMember) {

        if(null != teamMember.getUuid()) {
            TeamMember updatedTeamMember = teamMemberRepository.update(teamMember);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedTeamMember.getUuid())))
                    .body(updatedTeamMember);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/team-member/" + uuid);
    }
}





