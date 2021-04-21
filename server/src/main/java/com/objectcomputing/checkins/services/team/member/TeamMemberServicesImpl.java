package com.objectcomputing.checkins.services.team.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {

    private final TeamRepository teamRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberHistoryRepository memberHistoryRepository;
    private final ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(TeamMemberServicesImpl.class);

    public TeamMemberServicesImpl(TeamRepository teamRepo,
                                  TeamMemberRepository teamMemberRepo,
                                  MemberProfileRepository memberRepo,
                                  CurrentUserServices currentUserServices,
                                  MemberHistoryRepository memberHistoryRepository,
                                  ObjectMapper objectMapper) {
        this.teamRepo = teamRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.memberHistoryRepository = memberHistoryRepository;
        this.objectMapper = objectMapper;
    }

    // Helper function
    private static MemberHistory buildMemberHistory(UUID teamId, UUID memberId, String change, LocalDateTime date) {
        return new MemberHistory(teamId, memberId, change, LocalDateTime.now());
    }

    public TeamMember save(@Valid @NotNull TeamMember teamMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID teamId = teamMember.getTeamid();
        final UUID memberId = teamMember.getMemberid();
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isEmpty()) {
            throw new BadArgException(String.format("Team %s doesn't exist", teamId));
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        if (teamMember.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for team member", teamMember.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (teamMemberRepo.findByTeamidAndMemberid(teamMember.getTeamid(), teamMember.getMemberid()).isPresent()) {
            throw new BadArgException(String.format("Member %s already exists in team %s", memberId, teamId));
        } else if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberid().equals(currentUser.getId()))) {
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
        final UUID teamId = teamMember.getTeamid();
        final UUID memberId = teamMember.getMemberid();
        Optional<Team> team = teamRepo.findById(teamId);

        if (team.isEmpty()) {
            throw new BadArgException(String.format("Team %s doesn't exist", teamId));
        }

        Set<TeamMember> teamLeads = this.findByFields(teamId, null, true);

        Optional<TeamMember> originalTeamMember = teamMemberRepo.findById(id);
        if (id == null || originalTeamMember.isEmpty()) {
            throw new BadArgException(String.format("Unable to locate teamMember to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (teamMemberRepo.findByTeamidAndMemberid(teamMember.getTeamid(), teamMember.getMemberid()).isEmpty()) {
            throw new BadArgException(String.format("Member %s is not part of team %s", memberId, teamId));
        } else if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberid().equals(currentUser.getId()))) {
            throw new BadArgException("You are not authorized to perform this operation");
        }

        TeamMember updated = teamMemberRepo.update(teamMember);

        try {
            String originalTeamMemberString = objectMapper.writeValueAsString(originalTeamMember.get());
            String newTeamMemberString = objectMapper.writeValueAsString(teamMember);
            String update = String.format("from %s to %s", originalTeamMemberString, newTeamMemberString);
            memberHistoryRepository.save(buildMemberHistory(teamId, memberId, "updated", LocalDateTime.now()));
        } catch (JsonProcessingException e) {
            //Log info on error here...include new team member id and team id in log message
            LOG.error("Error occurred while updating member profile. teamId = %s , memberId = %s", teamId, memberId, e);
        }
        return updated;
    }

    public Set<TeamMember> findByFields(@Nullable UUID teamid, @Nullable UUID memberid, @Nullable Boolean lead) {
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

    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        TeamMember teamMember = teamMemberRepo.findById(id).orElse(null);
        if (teamMember != null) {
            Set<TeamMember> teamLeads = this.findByFields(teamMember.getTeamid(), null, true);

            if (!isAdmin && teamLeads.stream().noneMatch(o -> o.getMemberid().equals(currentUser.getId()))) {
                throw new PermissionException("You are not authorized to perform this operation");
            } else {
                teamMemberRepo.deleteById(id);
            }
        } else {
            throw new NotFoundException(String.format("Unable to locate teamMember with id %s", id));
        }

        teamMemberRepo.delete(teamMember);
        memberHistoryRepository.save(buildMemberHistory(teamMember.getTeamid(), teamMember.getMemberid(), "Deleted", LocalDateTime.now()));
    }
}