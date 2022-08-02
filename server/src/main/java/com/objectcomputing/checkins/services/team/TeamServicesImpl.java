package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.*;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.util.Validation.validate;

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

            validate(teamsRepo.search(teamDTO.getName(), null).isEmpty()).orElseThrow(() -> {
                throw new AlreadyExistsException("Team with name %s already exists", teamDTO.getName());
            });
            validate(teamDTO.getTeamMembers() != null && teamDTO.getTeamMembers().stream().anyMatch(TeamCreateDTO.TeamMemberCreateDTO::getLead)).orElseThrow(() -> {
                throw new BadArgException("Team must include at least one team lead");
            });

            newTeamEntity = teamsRepo.save(fromDTO(teamDTO));
            for (TeamCreateDTO.TeamMemberCreateDTO memberDTO : teamDTO.getTeamMembers()) {
                MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(memberDTO, newTeamEntity.getId())), existingMember));
            }
        }

        return fromEntity(newTeamEntity, newMembers);
    }

    public TeamResponseDTO read(@NotNull UUID teamId) {
        Team foundTeam = teamsRepo.findById(teamId).orElseThrow(() -> {
            throw new NotFoundException("No such team found");
        });

        List<TeamMemberResponseDTO> teamMembers = teamMemberServices
                .findByFields(teamId, null, null)
                .stream()
                .filter(teamMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(teamMember.getMemberId()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(teamMember ->
                        fromMemberEntity(teamMember, memberProfileServices.getById(teamMember.getMemberId()))).collect(Collectors.toList());

        return fromEntity(foundTeam, teamMembers);
    }

    public TeamResponseDTO update(TeamUpdateDTO teamDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        boolean isTeamLead = currentUser != null && !teamMemberServices.findByFields(teamDTO.getId(), currentUser.getId(), true).isEmpty();
        validate(isAdmin || isTeamLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        TeamResponseDTO updated = null;
        List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
        if (teamDTO != null) {

            validate(teamDTO.getId() != null && teamsRepo.findById(teamDTO.getId()).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Team ID %s does not exist, can't update.", teamDTO.getId());
            });
            validate(teamDTO.getTeamMembers() != null && teamDTO.getTeamMembers().stream().anyMatch(TeamUpdateDTO.TeamMemberUpdateDTO::getLead)).orElseThrow(() -> {
                throw new BadArgException("Team must include at least one team lead");
            });

            Team newTeamEntity = teamsRepo.update(fromDTO(teamDTO));

            Set<TeamMember> existingTeamMembers = teamMemberServices.findByFields(teamDTO.getId(), null, null);
            //add any new members & updates
            teamDTO.getTeamMembers().forEach((updatedMember) -> {
                Optional<TeamMember> first = existingTeamMembers.stream().filter((existing) -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                if (first.isEmpty()) {
                    newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                } else {
                    newMembers.add(fromMemberEntity(teamMemberServices.update(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                }
            });

            //delete any removed members
            existingTeamMembers.forEach((existingMember) -> {
                if (teamDTO.getTeamMembers().stream().noneMatch((updatedTeamMember) -> updatedTeamMember.getMemberId().equals(existingMember.getMemberId()))) {
                    teamMemberServices.delete(existingMember.getId());
                }
            });
            updated = fromEntity(newTeamEntity, newMembers);
        }
        return updated;
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
        boolean isAdmin = currentUserServices.isAdmin();
        boolean isTeamLead = currentUser != null && !teamMemberServices.findByFields(id, currentUser.getId(), true).isEmpty();
        validate(isAdmin || isTeamLead).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to perform this operation");
        });

        teamMemberServices.deleteByTeam(id);
        teamsRepo.deleteById(id);
        return true;
    }

    private Team fromDTO(TeamUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(dto.getId(), dto.getName(), dto.getDescription());
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
        TeamResponseDTO dto = new TeamResponseDTO(entity.getId(), entity.getName(), entity.getDescription());
        dto.setTeamMembers(memberEntities);
        return dto;
    }

    private Team fromDTO(TeamCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(null, dto.getName(), dto.getDescription());
    }

    private TeamMemberResponseDTO fromMemberEntity(TeamMember teamMember, MemberProfile memberProfile) {
        if (teamMember == null || memberProfile == null) {
            return null;
        }
        return new TeamMemberResponseDTO(teamMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), teamMember.isLead());
    }
}
