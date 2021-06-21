package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

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
        List<TeamMemberResponseDTO> teamMembers = teamMemberServices
                .findByFields(teamId, null, null)
                .stream()
                .map(teamMember ->
                        fromMemberEntity(teamMember, memberProfileServices.getById(teamMember.getMemberId()))).collect(Collectors.toList());
        return fromEntity(teamsRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("No such team found")));
    }

    public TeamResponseDTO update(TeamUpdateDTO teamDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null &&
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
                    teamDTO.getTeamMembers().stream().forEach((updatedMember) -> {
                        Optional<TeamMember> first = existingTeamMembers.stream().filter((existing) -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                        if(!first.isPresent()) {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                        } else {
                            teamMemberServices.update(fromMemberDTO(updatedMember,newTeamEntity.getId()));
                        }
                    });

                    //delete any removed members
                    existingTeamMembers.stream().forEach((existingMember) -> {
                        if(!teamDTO.getTeamMembers().stream().filter((updatedTeamMember) -> updatedTeamMember.getMemberId().equals(existingMember.getMemberId())).findFirst().isPresent()) {
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
            throw new PermissionException("You are not authorized to perform this operation");
        }
    }

    public Set<TeamResponseDTO> findByFields(String name, UUID memberid) {
        Set<TeamResponseDTO> foundTeams = teamsRepo.search(name, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (TeamResponseDTO foundTeam : foundTeams) {
            Set<TeamMember> foundMembers = teamMemberServices.findByFields(foundTeam.getId(), null, null);
            for (TeamMember foundMember : foundMembers) {
                foundTeam.getTeamMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberId())));
            }
        }
        return foundTeams;
    }

    public boolean delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null && !teamMemberServices.findByFields(id, currentUser.getId(), true).isEmpty())) {
            teamMemberServices.deleteByTeam(id);
            teamsRepo.deleteById(id);
        } else {
            throw new PermissionException("You are not authorized to perform this operation");
        }
        return true;
    }

    private Team fromDTO(TeamUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(dto.getId(), dto.getName(), dto.getDescription());
    }

    private TeamMember fromMemberDTO(TeamCreateDTO.TeamMemberCreateDTO memberDTO, UUID teamId) {
        return new TeamMember(teamId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private TeamMember fromMemberDTO(TeamMemberResponseDTO memberDTO, UUID teamId, MemberProfile savedMember) {
        return new TeamMember(memberDTO.getId() == null ? null : memberDTO.getId(), teamId, savedMember.getId(), memberDTO.isLead());
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
