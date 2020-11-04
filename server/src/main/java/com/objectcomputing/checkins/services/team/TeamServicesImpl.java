package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.team.member.TeamMemberDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.security.utils.SecurityService;
import com.objectcomputing.checkins.services.role.RoleType;
import nu.studer.sample.tables.pojos.Team;
import nu.studer.sample.tables.pojos.TeamMember;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private final TeamRepository teamsRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    
    public TeamServicesImpl(TeamRepository teamsRepo,
                            TeamMemberRepository teamMemberRepo,
                            SecurityService securityService,
                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices) {
        this.teamsRepo = teamsRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    public TeamResponseDTO save(TeamCreateDTO teamDTO) {
        Team newTeamEntity = null;
        if (teamDTO != null) {
            if (teamsRepo.findByName(teamDTO.getName()).isPresent()) {
                throw new TeamBadArgException(String.format("Team with name %s already exists", teamDTO.getName()));
            } else {
                newTeamEntity = teamsRepo.save(fromDTO(teamDTO));
            }
        }

        return fromEntity(newTeamEntity);
    }

    public TeamResponseDTO read(@NotNull UUID teamId) {
        return fromEntity(teamsRepo.findById(teamId).orElseThrow(TeamNotFoundException::new));
    }

    public TeamResponseDTO update(TeamUpdateDTO teamEntity) {
        Team newTeamEntity = null;
        if (teamEntity != null) {
            if (teamEntity.getId() != null && teamsRepo.findById(teamEntity.getId()).isPresent()) {
                newTeamEntity = teamsRepo.update(fromDTO(teamEntity));
            } else {
                throw new TeamBadArgException(String.format("Team %s does not exist, can't update.", teamEntity.getId()));
            }
        }

        return fromEntity(newTeamEntity);
    }

    public Set<TeamResponseDTO> findByFields(String name, UUID memberid) {
        Set<TeamResponseDTO> foundTeams = teamsRepo.search(name, memberid).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins. May require not using the generated POJOs.
        for (TeamResponseDTO foundTeam : foundTeams) {
            List<TeamMember> foundMembers = teamMemberRepo.findByTeamid(foundTeam.getId());
            for (TeamMember foundMember : foundMembers) {
                foundTeam.getTeamMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(UUID.fromString(foundMember.getMemberid()))));
            }
        }
        return foundTeams;
    }

    public boolean delete(@NotNull UUID id) {
        String workEmail = securityService!=null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfileEntity currentUser = workEmail!=null? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService!=null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        List<TeamMember> CurrentTeam = teamMemberRepo.search(id, currentUser.getId(), true);
        if(isAdmin || !CurrentTeam.isEmpty()) {
            teamMemberRepo.deleteByTeamId(id);
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
        return new Team(dto.getId().toString(), dto.getName(), dto.getDescription());
    }

    private TeamResponseDTO fromEntity(Team entity) {
        if (entity == null) {
            return null;
        }
        return new TeamResponseDTO(UUID.fromString(entity.getId()), entity.getName(), entity.getDescription());
    }

    private Team fromDTO(TeamCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Team(null, dto.getName(), dto.getDescription());
    }

    private TeamMemberDTO fromMemberEntity(TeamMember teamMember, MemberProfileEntity memberProfile) {
        if (teamMember == null || memberProfile == null) {
            return null;
        }
        return new TeamMemberDTO(teamMember.getId(), memberProfile.getName(), teamMember.getLead() );
    }
}
