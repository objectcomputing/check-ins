package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.security.utils.SecurityService;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private TeamRepository teamsRepo;
    private TeamMemberRepository teamMemberRepo;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;
    private TeamMemberServices teamMemberServices;
    
    public TeamServicesImpl(TeamRepository teamsRepo, TeamMemberRepository teamMemberRepo,
                            SecurityService securityService, CurrentUserServices currentUserServices,
                            TeamMemberServices teamMemberServices) {
        this.teamsRepo = teamsRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.teamMemberServices = teamMemberServices;
    }

    public Team save(Team team) {
        Team newTeam = null;
        if (team != null) {
            if (team.getId() != null) {
                throw new TeamBadArgException(String.format("Found unexpected id %s, please try updating instead",
                        team.getId()));
            } else if (teamsRepo.findByName(team.getName()).isPresent()) {
                throw new TeamBadArgException(String.format("Team with name %s already exists", team.getName()));
            } else {
                newTeam = teamsRepo.save(team);
            }
        }

        return newTeam;
    }

    public Team read(UUID teamId) {
        return teamId != null ? teamsRepo.findById(teamId).orElse(null) : null;
    }

    public Team update(Team team) {
        Team newTeam = null;
        if (team != null) {
            if (team.getId() != null && teamsRepo.findById(team.getId()).isPresent()) {
                newTeam = teamsRepo.update(team);
            } else {
                throw new TeamBadArgException(String.format("Team %s does not exist, can't update.", team.getId()));
            }
        }

        return newTeam;
    }

    public Set<Team> findByFields(String name, UUID memberid) {
        Set<Team> teams = new HashSet<>();
        teamsRepo.findAll().forEach(teams::add);
        if (name != null) {
            teams.retainAll(teamsRepo.findByNameIlike(name));
        }
        if (memberid != null) {
            teams.retainAll(teamMemberRepo.findByMemberid(memberid)
                    .stream().map(TeamMember::getTeamid).map(gid -> teamsRepo.findById(gid).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet()));
        }
        return teams;
    }

    // public void delete(@NotNull UUID id) {
    //     teamsRepo.deleteById(id);
    // }

    public void delete(@NotNull UUID id) {
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        Team team = teamsRepo.findById(id).get();

        Set<TeamMember> CurrentTeam = teamMemberServices.findByFields(team.getId(), currentUser.getId(), true);
        if(isAdmin || !CurrentTeam.isEmpty()) {
            teamsRepo.deleteById(id);
        } else {
            throw new TeamBadArgException("You are not authorized to perform this operation");
        }
    }
}
