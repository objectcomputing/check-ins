package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileDoesNotExistException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private final TeamRepository teamsRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final TeamMemberServices teamMemberServices;
    
    public TeamServicesImpl(TeamRepository teamsRepo,
                            TeamMemberRepository teamMemberRepo,
                            SecurityService securityService,
                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices,
                            TeamMemberServices teamMemberServices) {
        this.teamsRepo = teamsRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.teamMemberServices = teamMemberServices;
    }

    public TeamResponseDTO save(TeamCreateDTO teamDTO) {
        Team newTeamEntity = null;
        List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
        if (teamDTO != null) {
            if (teamsRepo.findByName(teamDTO.getName()).isPresent()) {
                throw new TeamBadArgException(String.format("Team with name %s already exists", teamDTO.getName()));
            } else {
                newTeamEntity = teamsRepo.save(fromDTO(teamDTO));
                if (teamDTO.getTeamMembers() != null) {
                    for (TeamMemberResponseDTO memberDTO : teamDTO.getTeamMembers()) {
                        try {
                            MemberProfile existingMember = memberProfileServices.findByName(memberDTO.getName());
                            newMembers.add(fromMemberEntity(teamMemberRepo.save(fromMemberDTO(memberDTO, newTeamEntity.getId(), existingMember)), existingMember));
                        } catch (MemberProfileDoesNotExistException mpdnee) {
                            throw new TeamBadArgException("No member profile found for name: " + memberDTO.getName());
                        }
                    }
                }
            }
        }

        return fromEntity(newTeamEntity, newMembers);
    }

    public TeamResponseDTO read(@NotNull UUID teamId) {
        List<TeamMemberResponseDTO> teamMembers = teamMemberRepo
                .findByTeamid(teamId)
                .stream()
                .map(teamMember ->
                        fromMemberEntity(teamMember, memberProfileServices.getById(teamMember.getMemberid()))).collect(Collectors.toList());
        return fromEntity(teamsRepo.findById(teamId).orElseThrow(TeamNotFoundException::new), teamMembers);
    }

    public TeamResponseDTO update(TeamUpdateDTO teamEntity) {
        Team newTeamEntity = null;
        List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
        if (teamEntity != null) {
            if (teamEntity.getId() != null && teamsRepo.findById(teamEntity.getId()).isPresent()) {
                teamMemberRepo.deleteByTeamId(teamEntity.getId().toString());
                newTeamEntity = teamsRepo.update(fromDTO(teamEntity));
                if (teamEntity.getTeamMembers() != null) {
                    for (TeamMemberResponseDTO memberDTO : teamEntity.getTeamMembers()) {
                        try {
                            MemberProfile existingMember = memberProfileServices.findByName(memberDTO.getName());
                            newMembers.add(fromMemberEntity(teamMemberRepo.save(fromMemberDTO(memberDTO, teamEntity.getId(), existingMember)), existingMember));
                        } catch (MemberProfileDoesNotExistException mpdnee) {
                            throw new TeamBadArgException("No member profile found for name: " + memberDTO.getName());
                        }
                    }
                }
            } else {
                throw new TeamBadArgException(String.format("Team %s does not exist, can't update.", teamEntity.getId()));
            }
        }

        return fromEntity(newTeamEntity, newMembers);
    }

    public Set<TeamResponseDTO> findByFields(String name, UUID memberid) {
        Set<TeamResponseDTO> foundTeams = teamsRepo.search(name, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (TeamResponseDTO foundTeam : foundTeams) {
            List<TeamMember> foundMembers = teamMemberRepo.findByTeamid(foundTeam.getId());
            for (TeamMember foundMember : foundMembers) {
                foundTeam.getTeamMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberid())));
            }
        }
        return foundTeams;
    }

    public boolean delete(@NotNull UUID id) {
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        List<TeamMember> CurrentTeam = teamMemberRepo.search(nullSafeUUIDToString(id), nullSafeUUIDToString(currentUser.getId()), true);
        if(isAdmin || !CurrentTeam.isEmpty()) {
            teamMemberRepo.deleteByTeamId(id.toString());
            teamsRepo.deleteById(id);
        } else {
            throw new TeamBadArgException("You are not authorized to perform this operation");
        }
        return true;
    }

    private Team fromDTO(TeamUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(dto.getId(), dto.getName(), dto.getDescription());
    }

    private TeamMember fromMemberDTO(TeamMemberResponseDTO memberDTO, UUID teamId, MemberProfile savedMember) {
        return new TeamMember(memberDTO.getId() == null ? null : memberDTO.getId(), teamId, savedMember.getId(), memberDTO.isLead());
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
        return new TeamMemberResponseDTO(teamMember.getId(), memberProfile.getName(), teamMember.isLead() );
    }
}
