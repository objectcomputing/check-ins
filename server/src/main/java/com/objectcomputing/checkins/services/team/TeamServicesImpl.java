package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamServicesImpl implements TeamServices {

    @Inject
    private TeamRepository teamsRepo;
    @Inject
    private TeamMemberRepository teamMemberRepo;

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
}
