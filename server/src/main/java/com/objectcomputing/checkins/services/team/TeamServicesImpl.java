package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private final TeamRepository teamsRepo;
    private final TeamMemberServices teamMemberServices;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public TeamServicesImpl(TeamRepository teamsRepo,
                            TeamMemberServices teamMemberServices,
                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices) {
        this.teamsRepo = teamsRepo;
        this.teamMemberServices = teamMemberServices;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    public TeamResponseDTO save(TeamCreateDTO teamDTO) {
        Team newTeamEntity = null;
        List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
        if (teamDTO != null) {
            if (!teamsRepo.search(teamDTO.getName(), null).isEmpty()) {
                throw new BadArgException(String.format("Team with name %s already exists", teamDTO.getName()));
            } else {
                if (teamDTO.getTeamMembers() == null ||
                        teamDTO.getTeamMembers().stream().noneMatch(TeamCreateDTO.TeamMemberCreateDTO::getLead)) {
                    throw new BadArgException("Team must include at least one team lead");
                }
                newTeamEntity = teamsRepo.save(fromDTO(teamDTO));
                for (TeamCreateDTO.TeamMemberCreateDTO memberDTO : teamDTO.getTeamMembers()) {
                    MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                    newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(memberDTO, newTeamEntity.getId())), existingMember));
                }
            }
        }

        return fromEntity(newTeamEntity, newMembers);
    }

    public TeamResponseDTO read(@NotNull UUID teamId) {
        Team foundTeam = teamsRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("No such team found"));

        List<TeamMemberResponseDTO> teamMembers = teamMemberServices
                .findByFields(teamId, null, null)
                .stream()
                .filter(teamMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(teamMember.getMemberId()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(teamMember ->
                        fromMemberEntity(teamMember, memberProfileServices.getById(teamMember.getMemberId())))
                .toList();

        return fromEntity(foundTeam, teamMembers);
    }

    public TeamResponseDTO update(TeamUpdateDTO teamDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (hasAdministerPermission() || (currentUser != null &&
                !teamMemberServices.findByFields(teamDTO.getId(), currentUser.getId(), true).isEmpty())) {

            TeamResponseDTO updated = null;
            List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
            if (teamDTO != null) {
                if (teamDTO.getId() != null && teamsRepo.findById(teamDTO.getId()).isPresent()) {
                    if (teamDTO.getTeamMembers() == null ||
                            teamDTO.getTeamMembers().stream().noneMatch(TeamUpdateDTO.TeamMemberUpdateDTO::getLead)) {
                        throw new BadArgException("Team must include at least one team lead");
                    }


                    Team newTeamEntity = teamsRepo.update(fromDTO(teamDTO));

                    Set<TeamMember> existingTeamMembers = teamMemberServices.findByFields(teamDTO.getId(), null, null);
                    //add any new members & updates
                    teamDTO.getTeamMembers().forEach(updatedMember -> {
                        Optional<TeamMember> first = existingTeamMembers.stream().filter(existing -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                        if (first.isEmpty()) {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                        } else {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(teamMemberServices.update(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                        }
                    });

                    //delete any removed members
                    existingTeamMembers.forEach(existingMember -> {
                        if (teamDTO.getTeamMembers().stream().noneMatch(updatedTeamMember -> updatedTeamMember.getMemberId().equals(existingMember.getMemberId()))) {
                            teamMemberServices.delete(existingMember.getId());
                        }
                    });

                    updated = fromEntity(newTeamEntity, newMembers);
                } else {
                    throw new BadArgException(String.format("Team ID %s does not exist, can't update.", teamDTO.getId()));
                }
            }
            return updated;
        } else {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
    }

    public Set<TeamResponseDTO> findByFields(String name, UUID memberid) {
        Set<TeamResponseDTO> foundTeams = teamsRepo.search(name, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (TeamResponseDTO foundTeam : foundTeams) {
            Set<TeamMember> foundMembers = teamMemberServices.findByFields(foundTeam.getId(), null, null).stream().filter(teamMember -> {
                LocalDate terminationDate = memberProfileServices.getById(teamMember.getMemberId()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (TeamMember foundMember : foundMembers) {
                foundTeam.getTeamMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberId())));
            }
        }
        return foundTeams;
    }

    public boolean delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        if (hasAdministerPermission() || (currentUser != null && !teamMemberServices.findByFields(id, currentUser.getId(), true).isEmpty())) {
            teamMemberServices.deleteByTeam(id);
            teamsRepo.deleteById(id);
        } else {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        return true;
    }

    private Team fromDTO(TeamUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(dto.getId(), dto.getName(), dto.getDescription(), dto.isActive());
    }

    private TeamMember fromMemberDTO(TeamCreateDTO.TeamMemberCreateDTO memberDTO, UUID teamId) {
        return new TeamMember(null, teamId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private TeamMember fromMemberDTO(TeamUpdateDTO.TeamMemberUpdateDTO memberDTO, UUID teamId) {
        return new TeamMember(memberDTO.getId(), teamId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private TeamResponseDTO fromEntity(Team entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private TeamResponseDTO fromEntity(Team entity, List<TeamMemberResponseDTO> memberEntities) {
        if (entity == null) {
            return null;
        }
        TeamResponseDTO dto = new TeamResponseDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.isActive());
        dto.setTeamMembers(memberEntities);
        return dto;
    }

    private Team fromDTO(TeamCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(null, dto.getName(), dto.getDescription(), dto.isActive());
    }

    private TeamMemberResponseDTO fromMemberEntity(TeamMember teamMember, MemberProfile memberProfile) {
        if (teamMember == null || memberProfile == null) {
            return null;
        }
        return new TeamMemberResponseDTO(teamMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), teamMember.isLead());
    }

    private boolean hasAdministerPermission() {
        return currentUserServices.hasPermission(Permission.CAN_ADMINISTER_TEAMS);
    }
}
