package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {

    private final TeamRepository teamRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;
    private final MemberHistoryRepository memberHistoryRepository;

    public TeamMemberServicesImpl(TeamRepository teamRepo,
                                  TeamMemberRepository teamMemberRepo,
                                  MemberProfileRetrievalServices memberProfileRetrievalServices,
                                  CurrentUserServices currentUserServices,
                                  MemberHistoryRepository memberHistoryRepository) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
        this.memberHistoryRepository = memberHistoryRepository;
    }

    // Helper function
    private static MemberHistory buildMemberHistory(UUID teamId, UUID memberId, String change, LocalDateTime date) {
        return new MemberHistory(teamId, memberId, change, LocalDateTime.now());
    }

    public TeamMember save(@Valid @NotNull TeamMember teamMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        final UUID teamId = teamMember.getTeamId();
        final UUID memberId = teamMember.getMemberId();

        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isEmpty()) {
            throw new BadArgException("Team %s doesn't exist", teamId);
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        if (teamMember.getId() != null) {
            throw new BadArgException("Found unexpected id %s for team member", teamMember.getId());
        } else if (memberProfileRetrievalServices.getById(memberId).isEmpty()) {
            throw new BadArgException("Member %s doesn't exist", memberId);
        } else if (teamMemberRepo.findByTeamIdAndMemberId(teamMember.getTeamId(), teamMember.getMemberId()).isPresent()) {
            throw new BadArgException("Member %s already exists in team %s", memberId, teamId);
        } else if (!isAdmin && teamLeads.size() > 0 && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException("You are not authorized to perform this operation");
        }

        TeamMember newTeamMember = teamMemberRepo.save(teamMember);
        memberHistoryRepository.save(buildMemberHistory(teamId, memberId, "Added", LocalDateTime.now()));
        return newTeamMember;
    }

    public TeamMember read(@NotNull UUID id) {
        return teamMemberRepo.findById(id).orElse(null);
    }

    public TeamMember update(@NotNull @Valid TeamMember teamMember) {

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = teamMember.getId();
        final UUID teamId = teamMember.getTeamId();
        final UUID memberId = teamMember.getMemberId();
        Optional<Team> team = teamRepo.findById(teamId);

        if (team.isEmpty()) {
            throw new BadArgException("Team %s doesn't exist", teamId);
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        if (id == null || teamMemberRepo.findById(id).isEmpty()) {
            throw new BadArgException("Unable to locate teamMember to update with id %s", id);
        } else if (memberProfileRetrievalServices.getById(memberId).isEmpty()) {
            throw new BadArgException("Member %s doesn't exist", memberId);
        } else if (teamMemberRepo.findByTeamIdAndMemberId(teamMember.getTeamId(), teamMember.getMemberId()).isEmpty()) {
            throw new BadArgException("Member %s is not part of team %s", memberId, teamId);
        } else if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException("You are not authorized to perform this operation");
        }

        TeamMember teamMemberUpdate = teamMemberRepo.update(teamMember);
        memberHistoryRepository.save(buildMemberHistory(teamId, memberId, "Updated", LocalDateTime.now()));
        return teamMemberUpdate;
    }

    public Set<TeamMember> findByFields(@Nullable UUID teamId, @Nullable UUID memberId, @Nullable Boolean lead) {
        Set<TeamMember> teamMembers = new HashSet<>();
        teamMemberRepo.findAll().forEach(teamMembers::add);

        if (teamId != null) {
            teamMembers.retainAll(teamMemberRepo.findByTeamId(teamId));
        }
        if (memberId != null) {
            teamMembers.retainAll(teamMemberRepo.findByMemberId(memberId));
        }
        if (lead != null) {
            teamMembers.retainAll(teamMemberRepo.findByLead(lead));
        }

        return teamMembers;
    }

    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        TeamMember teamMember = teamMemberRepo.findById(id).orElse(null);
        if (teamMember != null) {
            Set<TeamMember> teamLeads = this.findByFields(teamMember.getTeamId(), null, true);

            if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException("You are not authorized to perform this operation");
            } else {
                teamMemberRepo.deleteById(id);
            }
        } else {
            throw new NotFoundException("Unable to locate teamMember with id %s", id);
        }

        teamMemberRepo.delete(teamMember);
        memberHistoryRepository.save(buildMemberHistory(teamMember.getTeamId(), teamMember.getMemberId(), "Deleted", LocalDateTime.now()));
    }

    public void deleteByTeam(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        List<TeamMember> teamMembers = teamMemberRepo.findByTeamId(id);
        if (teamMembers != null) {
            List<TeamMember> teamLeads = teamMembers.stream().filter((member) -> member.isLead()).collect(Collectors.toList());

            if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException("You are not authorized to perform this operation");
            } else {
                teamMembers.forEach(member -> {
                    teamMemberRepo.deleteById(member.getId());
                    memberHistoryRepository.save(buildMemberHistory(member.getTeamId(), member.getMemberId(), "Team Deleted", LocalDateTime.now()));
                });
            }
        } else {
            throw new NotFoundException("Unable to locate team with id %s", id);
        }
    }
}