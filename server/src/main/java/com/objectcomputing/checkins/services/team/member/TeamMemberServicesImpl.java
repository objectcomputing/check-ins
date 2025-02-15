package com.objectcomputing.checkins.services.team.member;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {

    private final TeamRepository teamRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberHistoryRepository memberHistoryRepository;

    public TeamMemberServicesImpl(TeamRepository teamRepo,
                                  TeamMemberRepository teamMemberRepo,
                                  MemberProfileRepository memberRepo,
                                  CurrentUserServices currentUserServices,
                                  MemberHistoryRepository memberHistoryRepository) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.memberHistoryRepository = memberHistoryRepository;
    }

    // Helper function
    private static MemberHistory buildMemberHistory(UUID teamId, UUID memberId, String change, LocalDateTime date) {
        return new MemberHistory(teamId, memberId, change, LocalDateTime.now());
    }

    public TeamMember save(@Valid @NotNull TeamMember teamMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canAdminister = hasAdministerPermission();
        final UUID teamId = teamMember.getTeamId();
        final UUID memberId = teamMember.getMemberId();

        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isEmpty()) {
            throw new BadArgException(String.format("Team %s doesn't exist", teamId));
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        if (teamMember.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for team member", teamMember.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (teamMemberRepo.findByTeamIdAndMemberId(teamMember.getTeamId(), teamMember.getMemberId()).isPresent()) {
            throw new BadArgException(String.format("Member %s already exists in team %s", memberId, teamId));
        } else if (!canAdminister && teamLeads.size() > 0 && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException(NOT_AUTHORIZED_MSG);
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
        boolean canAdminister = hasAdministerPermission();

        final UUID id = teamMember.getId();
        final UUID teamId = teamMember.getTeamId();
        final UUID memberId = teamMember.getMemberId();
        Optional<Team> team = teamRepo.findById(teamId);

        if (team.isEmpty()) {
            throw new BadArgException(String.format("Team %s doesn't exist", teamId));
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        if (id == null || teamMemberRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate teamMember to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (teamMemberRepo.findByTeamIdAndMemberId(teamMember.getTeamId(), teamMember.getMemberId()).isEmpty()) {
            throw new BadArgException(String.format("Member %s is not part of team %s", memberId, teamId));
        } else if (!canAdminister && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
            throw new BadArgException(NOT_AUTHORIZED_MSG);
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
        boolean canAdminister = hasAdministerPermission();

        TeamMember teamMember = teamMemberRepo.findById(id).orElse(null);
        if (teamMember != null) {
            Set<TeamMember> teamLeads = this.findByFields(teamMember.getTeamId(), null, true);

            if (!canAdminister && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            } else {
                teamMemberRepo.deleteById(id);
            }
        } else {
            throw new NotFoundException(String.format("Unable to locate teamMember with id %s", id));
        }

        teamMemberRepo.delete(teamMember);
        memberHistoryRepository.save(buildMemberHistory(teamMember.getTeamId(), teamMember.getMemberId(), "Deleted", LocalDateTime.now()));
    }

    public void deleteByTeam(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean canAdminister = hasAdministerPermission();

        List<TeamMember> teamMembers = teamMemberRepo.findByTeamId(id);
        if (teamMembers != null) {
            List<TeamMember> teamLeads = teamMembers.stream().filter(TeamMember::isLead).toList();

            if (!canAdminister && teamLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            } else {
                teamMembers.forEach(member -> {
                    teamMemberRepo.deleteById(member.getId());
                    memberHistoryRepository.save(buildMemberHistory(member.getTeamId(), member.getMemberId(), "Team Deleted", LocalDateTime.now()));
                });
            }
        } else {
            throw new NotFoundException(String.format("Unable to locate team with id %s", id));
        }
    }

    private boolean hasAdministerPermission() {
        return currentUserServices.hasPermission(Permission.CAN_ADMINISTER_TEAMS);
    }
}
