package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class TeamServicesImpl implements TeamServices {

    private final TeamRepository teamsRepo;
    private final TeamMemberServices teamMemberServices;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;

    public TeamServicesImpl(TeamRepository teamsRepo,
                            TeamMemberServices teamMemberServices,
                            CurrentUserServices currentUserServices,
                            MemberProfileRetrievalServices memberProfileRetrievalServices) {
        this.teamsRepo = teamsRepo;
        this.teamMemberServices = teamMemberServices;
        this.currentUserServices = currentUserServices;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
    }

    public TeamResponseDTO save(TeamCreateDTO teamDTO) {
        Team newTeamEntity = null;
        List<TeamMemberResponseDTO> newMembers = new ArrayList<>();
        if (teamDTO != null) {
            if (!teamsRepo.search(teamDTO.getName(), null).isEmpty()) {
                throw new BadArgException("Team with name %s already exists", teamDTO.getName());
            } else {
                if (teamDTO.getTeamMembers() == null ||
                        teamDTO.getTeamMembers().stream().noneMatch(TeamCreateDTO.TeamMemberCreateDTO::getLead)) {
                    throw new BadArgException("Team must include at least one team lead");
                }
                newTeamEntity = teamsRepo.save(fromDTO(teamDTO));
                for (TeamCreateDTO.TeamMemberCreateDTO memberDTO : teamDTO.getTeamMembers()) {
                    MemberProfile existingMember = memberProfileRetrievalServices.getById(memberDTO.getMemberId()).orElseThrow(() -> {
                        throw new BadArgException("Team member %s does not exist", memberDTO.getMemberId());
                    });
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
                    LocalDate terminationDate = memberProfileRetrievalServices.getById(teamMember.getMemberId()).orElseThrow(() -> {
                        throw new BadArgException("Team member %s does not exist", teamMember.getMemberId());
                    }).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(teamMember ->
                        fromMemberEntity(teamMember, memberProfileRetrievalServices.getById(teamMember.getMemberId()).orElseThrow(() -> {
                            throw new BadArgException("Team member %s does not exist", teamMember.getMemberId());
                        }))).collect(Collectors.toList());

        return fromEntity(foundTeam, teamMembers);
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
                        MemberProfile existingMember = memberProfileRetrievalServices.getById(updatedMember.getMemberId()).orElseThrow(() -> {
                            throw new BadArgException("Team member %s does not exist", updatedMember.getMemberId());
                        });
                        if (first.isEmpty()) {
                            newMembers.add(fromMemberEntity(teamMemberServices.save(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                        } else {
                            newMembers.add(fromMemberEntity(teamMemberServices.update(fromMemberDTO(updatedMember, newTeamEntity.getId())), existingMember));
                        }
                    });

                    //delete any removed members
                    existingTeamMembers.stream().forEach((existingMember) -> {
                        if (!teamDTO.getTeamMembers().stream().filter((updatedTeamMember) -> updatedTeamMember.getMemberId().equals(existingMember.getMemberId())).findFirst().isPresent()) {
                            teamMemberServices.delete(existingMember.getId());
                        }
                    });

                    updated = fromEntity(newTeamEntity, newMembers);
                } else {
                    throw new BadArgException("Team ID %s does not exist, can't update.", teamDTO.getId());
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
            Set<TeamMember> foundMembers = teamMemberServices.findByFields(foundTeam.getId(), null, null).stream().filter(teamMember -> {
                LocalDate terminationDate = memberProfileRetrievalServices.getById(teamMember.getMemberId()).orElseThrow(() -> {
                    throw new BadArgException("Team member %s does not exist", teamMember.getMemberId());
                }).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (TeamMember foundMember : foundMembers) {
                foundTeam.getTeamMembers().add(fromMemberEntity(foundMember, memberProfileRetrievalServices.getById(foundMember.getMemberId()).orElseThrow(() -> {
                    throw new BadArgException("Team member %s does not exist", foundMember.getMemberId());
                })));
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
