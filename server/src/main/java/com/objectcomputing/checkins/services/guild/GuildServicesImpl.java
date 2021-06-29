package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.guild.member.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class GuildServicesImpl implements GuildServices {

    private final GuildRepository guildsRepo;
    private final GuildMemberRepository guildMemberRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final GuildMemberServices guildMemberServices;

    public GuildServicesImpl(GuildRepository guildsRepo,
                             GuildMemberRepository guildMemberRepo,
                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices,
                             GuildMemberServices guildMemberServices) {
        this.guildsRepo = guildsRepo;
        this.guildMemberRepo = guildMemberRepo;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.guildMemberServices = guildMemberServices;
    }

    public GuildResponseDTO save(GuildCreateDTO guildDTO) {
        Guild newGuildEntity = null;
        List<GuildMemberResponseDTO> newMembers = new ArrayList<>();
        if (guildDTO != null) {
            if (!guildsRepo.search(guildDTO.getName(), null).isEmpty()) {
                throw new BadArgException(String.format("Guild with name %s already exists", guildDTO.getName()));
            } else {
                if (guildDTO.getGuildMembers() == null ||
                        guildDTO.getGuildMembers().stream().noneMatch(GuildCreateDTO.GuildMemberCreateDTO::getLead)) {
                    throw new BadArgException("Guild must include at least one guild lead");
                }
                newGuildEntity = guildsRepo.save(fromDTO(guildDTO));
                for (GuildCreateDTO.GuildMemberCreateDTO memberDTO : guildDTO.getGuildMembers()) {
                    MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                    newMembers.add(fromMemberEntity(guildMemberRepo.save(fromMemberDTO(memberDTO, newGuildEntity.getId())), existingMember));
                }
            }
        }

        return fromEntity(newGuildEntity, newMembers);
    }

    public GuildResponseDTO read(@NotNull UUID guildId) {
        Guild foundGuild = guildsRepo.findById(guildId)
                .orElseThrow(() -> new NotFoundException("No such guild found"));

        List<GuildMemberResponseDTO> guildMembers = guildMemberRepo
                .findByGuildid(guildId)
                .stream()
                .filter(guildMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(guildMember.getMemberid()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(guildMember ->
                        fromMemberEntity(guildMember, memberProfileServices.getById(guildMember.getMemberid()))).collect(Collectors.toList());

        return fromEntity(foundGuild, guildMembers);
    }

    public GuildResponseDTO update(GuildUpdateDTO guildDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null &&
                !guildMemberServices.findByFields(guildDTO.getId(), currentUser.getId(), true).isEmpty())) {
            // Guild newGuildEntity = null;
            GuildResponseDTO updated= null;
            List<GuildMemberResponseDTO> newMembers = new ArrayList<>();
            if (guildDTO != null) {
                if (guildDTO.getId() != null && guildsRepo.findById(guildDTO.getId()).isPresent()) {
                    if (guildDTO.getGuildMembers() == null ||
                            guildDTO.getGuildMembers().stream().noneMatch(GuildUpdateDTO.GuildMemberUpdateDTO::getLead)) {
                        throw new BadArgException("Guild must include at least one guild lead");
                    }

                    Guild newGuildEntity  = guildsRepo.update(fromDTO(guildDTO));
                    Set<GuildMember> existingGuildMembers = guildMemberServices.findByFields(guildDTO.getId(), null, null);
                    //add new members to the guild
                    guildDTO.getGuildMembers().stream().forEach((updatedMember) -> {
                        Optional<GuildMember> first = existingGuildMembers.stream().filter((existing) -> existing.getMemberid().equals(updatedMember.getMemberId())).findFirst();
                        if(!first.isPresent()) {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(guildMemberServices.save(fromMemberDTO(updatedMember, newGuildEntity.getId())), existingMember));
                        } else {
                            ;
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(guildMemberServices.update(fromMemberDTO(updatedMember, newGuildEntity.getId())), existingMember));
                        }
                    });

                    //delete any removed members from guild
                    existingGuildMembers.stream().forEach((existingMember) -> {
                        if(!guildDTO.getGuildMembers().stream().filter((updatedTeamMember) -> updatedTeamMember.getMemberId().equals(existingMember.getMemberid())).findFirst().isPresent()) {
                            guildMemberServices.delete(existingMember.getId());
                        }
                    });

                    updated = fromEntity(newGuildEntity, newMembers);
                } else {
                    throw new BadArgException(String.format("Guild ID %s does not exist, can't update.", guildDTO.getId()));
                }
            }

            return updated;
        } else {
            throw new PermissionException("You are not authorized to perform this operation");
        }
    }


    public Set<GuildResponseDTO> findByFields(String name, UUID memberid) {
        Set<GuildResponseDTO> foundGuilds = guildsRepo.search(name, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (GuildResponseDTO foundGuild : foundGuilds) {
            Set<GuildMember> foundMembers = guildMemberRepo.findByGuildid(foundGuild.getId()).stream().filter(guildMember -> {
                LocalDate terminationDate = memberProfileServices.getById(guildMember.getMemberid()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (GuildMember foundMember : foundMembers) {
                foundGuild.getGuildMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberid())));
            }
        }
        return foundGuilds;
    }

    public boolean delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null && !guildMemberRepo.search(nullSafeUUIDToString(id), nullSafeUUIDToString(currentUser.getId()), true).isEmpty())) {
            guildMemberRepo.deleteByGuildId(id.toString());
            guildsRepo.deleteById(id);
        } else {
            throw new PermissionException("You are not authorized to perform this operation");
        }
        return true;
    }

    private Guild fromDTO(GuildUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Guild(dto.getId(), dto.getName(), dto.getDescription());
    }

    private GuildMember fromMemberDTO(GuildCreateDTO.GuildMemberCreateDTO memberDTO, UUID guildId) {
        return new GuildMember(null, guildId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private GuildMember fromMemberDTO(GuildMemberResponseDTO memberDTO, UUID guildId, MemberProfile savedMember) {
        return new GuildMember(memberDTO.getId() == null ? null : memberDTO.getId(), guildId, savedMember.getId(), memberDTO.isLead());
    }

    private GuildMember fromMemberDTO(GuildUpdateDTO.GuildMemberUpdateDTO memberDTO, UUID guildId) {
        return new GuildMember(memberDTO.getId(), guildId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private GuildResponseDTO fromEntity(Guild entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private GuildResponseDTO fromEntity(Guild entity, List<GuildMemberResponseDTO> memberEntities) {
        if (entity == null) {
            return null;
        }
        GuildResponseDTO dto = new GuildResponseDTO(entity.getId(), entity.getName(), entity.getDescription());
        dto.setGuildMembers(memberEntities);
        return dto;
    }

    private Guild fromDTO(GuildCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Guild(null, dto.getName(), dto.getDescription());
    }

    private GuildMemberResponseDTO fromMemberEntity(GuildMember guildMember, MemberProfile memberProfile) {
        if (guildMember == null || memberProfile == null) {
            return null;
        }
        return new GuildMemberResponseDTO(guildMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), guildMember.isLead());
    }
}