package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.team.TeamBadArgException;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {

    private final TeamRepository teamRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final MemberProfileRepository memberRepo;

    public TeamMemberServicesImpl(TeamRepository teamRepo,
                                  TeamMemberRepository teamMemberRepo,
                                  MemberProfileRepository memberRepo) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.memberRepo = memberRepo;
    }


    public TeamMember save(TeamMember teamMember) {
        TeamMember teamMemberRet = null;
        if (teamMember != null) {
            final UUID teamId = teamMember.getTeamid();
            final UUID memberId = teamMember.getMemberid();
            if (teamId == null || memberId == null) {
                throw new TeamBadArgException(String.format("Invalid teamMember %s", teamMember));
            } else if (teamMember.getId() != null) {
                throw new TeamBadArgException(String.format("Found unexpected id %s for team member", teamMember.getId()));
            } else if (teamRepo.findById(teamId).isEmpty()) {
                throw new TeamBadArgException(String.format("Team %s doesn't exist", teamId));
            } else if (memberRepo.findById(memberId).isEmpty()) {
                throw new TeamBadArgException(String.format("Member %s doesn't exist", memberId));
            } else if (teamMemberRepo.findByTeamidAndMemberid(teamMember.getTeamid(),
                    teamMember.getMemberid()).isPresent()) {
                throw new TeamBadArgException(String.format("Member %s already exists in team %s", memberId, teamId));
            }

            teamMemberRet = teamMemberRepo.save(teamMember);
        }
        return teamMemberRet;
    }

    public TeamMember read(@NotNull UUID id) {
        return teamMemberRepo.findById(id).orElse(null);
    }

    public TeamMember update(TeamMember teamMember) {
        TeamMember teamMemberRet = null;
        if (teamMember != null) {
            final UUID id = teamMember.getId();
            final UUID teamId = teamMember.getTeamid();
            final UUID memberId = teamMember.getMemberid();
            if (teamId == null || memberId == null) {
                throw new TeamBadArgException(String.format("Invalid teamMember %s", teamMember));
            } else if (id == null || !teamMemberRepo.findById(id).isPresent()) {
                throw new TeamBadArgException(String.format("Unable to locate teamMember to update with id %s", id));
            } else if (!teamRepo.findById(teamId).isPresent()) {
                throw new TeamBadArgException(String.format("Team %s doesn't exist", teamId));
            } else if (!memberRepo.findById(memberId).isPresent()) {
                throw new TeamBadArgException(String.format("Member %s doesn't exist", memberId));
            }

            teamMemberRet = teamMemberRepo.update(teamMember);
        }
        return teamMemberRet;
    }

    public Set<TeamMember> findByFields(UUID teamid, UUID memberid, Boolean lead) {
        Set<TeamMember> teamMembers = new HashSet<>();
        teamMemberRepo.findAll().forEach(teamMembers::add);

        if (teamid != null) {
            teamMembers.retainAll(teamMemberRepo.findByTeamid(teamid));
        }
        if (memberid != null) {
            teamMembers.retainAll(teamMemberRepo.findByMemberid(memberid));
        }
        if (lead != null) {
            teamMembers.retainAll(teamMemberRepo.findByLead(lead));
        }

        return teamMembers;
    }
}