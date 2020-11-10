package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.team.TeamBadArgException;
import com.objectcomputing.checkins.services.team.TeamRepository;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {

    private TeamRepository teamRepo;
    private TeamMemberRepository teamMemberRepo;
    private MemberProfileRepository memberRepo;

    public TeamMemberServicesImpl(TeamRepository teamRepo,
                                  TeamMemberRepository teamMemberRepo,
                                  MemberProfileRepository memberRepo) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    public TeamMember save(@Valid @NotNull TeamMember teamMember) {
        final UUID teamId = teamMember.getTeamid();
        final UUID memberId = teamMember.getMemberid();
        final UUID supervisorId = teamMember.getSupervisorid();

        if (teamId == null || memberId == null) {
            throw new TeamBadArgException(String.format("Invalid teamMember %s", teamMember));
        } else if (teamMember.getId() != null) {
            throw new TeamBadArgException(String.format("Found unexpected id %s for team member", teamMember.getId()));
        } else if (!teamRepo.findById(teamId).isPresent()) {
            throw new TeamBadArgException(String.format("Team %s doesn't exist", teamId));
        } else if (!memberRepo.findById(memberId).isPresent()) {
            throw new TeamBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (teamMemberRepo.findByTeamidAndMemberid(teamMember.getTeamid(),
                teamMember.getMemberid()).isPresent()) {
            throw new TeamBadArgException(String.format("Member %s already exists in team %s", memberId, teamId));
        } else if (supervisorId != null && !memberRepo.findById(supervisorId).isPresent()) {
            throw new TeamBadArgException(String.format("Supervisor %s doesn't exist", supervisorId));
        }

        return teamMemberRepo.save(teamMember);
    }

    @Override
    public TeamMember read(@NotNull UUID id) {
        return teamMemberRepo.findById(id).orElse(null);
    }

    @Override
    public TeamMember update(@Valid @NotNull TeamMember teamMember) {
        final UUID id = teamMember.getId();
        final UUID teamId = teamMember.getTeamid();
        final UUID memberId = teamMember.getMemberid();
        final UUID supervisorId = teamMember.getSupervisorid();

        if (teamId == null || memberId == null) {
            throw new TeamBadArgException(String.format("Invalid teamMember %s", teamMember));
        } else if (id == null || !teamMemberRepo.findById(id).isPresent()) {
            throw new TeamBadArgException(String.format("Unable to locate teamMember to update with id %s", id));
        } else if (!teamRepo.findById(teamId).isPresent()) {
            throw new TeamBadArgException(String.format("Team %s doesn't exist", teamId));
        } else if (!memberRepo.findById(memberId).isPresent()) {
            throw new TeamBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (supervisorId != null && !memberRepo.findById(supervisorId).isPresent()) {
            throw new TeamBadArgException(String.format("Supervisor %s doesn't exist", supervisorId));
        }

        return teamMemberRepo.update(teamMember);
    }

    @Override
    public Set<TeamMember> findByFields(@Nullable UUID teamid,
                                        @Nullable UUID memberid,
                                        @Nullable Boolean lead,
                                        @Nullable UUID supervisorid) {

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
        if (supervisorid != null) {
            teamMembers.retainAll(teamMemberRepo.findBySupervisorid(supervisorid));
        }

        return teamMembers;
    }
}
