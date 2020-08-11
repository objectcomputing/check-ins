package com.objectcomputing.checkins.services.teammembers;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.Valid;

import com.objectcomputing.checkins.services.team.Team;
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

@Controller(TeamMemberController.TEAM_MEMBER_CONTROLLER_PATH)
@Secured(SecurityRule.IS_ANONYMOUS)
// @Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="team-member")
public class TeamMemberController {
    
    public static final String TEAM_MEMBER_CONTROLLER_PATH = "/services/team-member";

    //protected final TeamMemberRepository teamMemberRepository;
    protected final TeamMemberServices teamMemberServices;

    public TeamMemberController(//TeamMemberRepository teamMemberRepository,
                                TeamMemberServices teamMemberServices){
        //this.teamMemberRepository = teamMemberRepository;
        this.teamMemberServices = teamMemberServices;
    }
    /**
     * Find Team member by team, member or find all.
     * @param teamId
     * @param memberId
     * @return
     */
    @Get("/{?teamId,memberId}")
    public List<TeamMemberDTO> findByValue(@Nullable UUID teamId, @Nullable UUID memberId) {

        return teamMemberServices.findByTeamAndMember(teamId, memberId).stream().map(teamMember ->
                new TeamMemberDTO(teamMember.getUuid(), teamMember.getTeamId(), teamMember.getMemberId(), teamMember.getIsLead()))
                .collect(Collectors.toList());

        /*if(teamId != null) {
            return teamMemberRepository.findByTeamId(teamId);
        } else if(memberId != null) {
            return teamMemberRepository.findByMemberId(memberId);
        } else {
            return teamMemberRepository.findAll();
        }*/
    }

    /**
     * Save a new team member.
     * @param teamMember
     * @return
     */
    @Post
    public HttpResponse<TeamMemberDTO> save(@Body @Valid TeamMemberDTO teamMember) {
        //TeamMember newTeamMember = teamMemberRepository.save(teamMember);
        TeamMember newTeamMember = teamMemberServices.saveTeamMember(new TeamMember(teamMember.getTeamId(), teamMember.getMemberId(), teamMember.isLead()));
        return HttpResponse
                .created(new TeamMemberDTO(newTeamMember.getUuid(), newTeamMember.getTeamId(), newTeamMember.getMemberId(), newTeamMember.getIsLead()))
                .headers(headers -> headers.location(location(newTeamMember.getUuid())));
    }

    /**
     * Update a Team member.
     * @param teamMember
     * @return
     */
    @Put
    public HttpResponse<?> update(@Body @Valid TeamMemberDTO teamMember) {

        if(null != teamMember.getUuid()) {
            //TeamMember updatedTeamMember = teamMemberRepository.update(teamMember);
            TeamMember updatedTeamMember = teamMemberServices.updateTeamMember(new TeamMember(teamMember.getTeamId(),
                    teamMember.getMemberId(), teamMember.getUuid(), teamMember.isLead()));
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedTeamMember.getUuid())))
                    .body(updatedTeamMember);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create(TEAM_MEMBER_CONTROLLER_PATH + uuid);
    }
}





