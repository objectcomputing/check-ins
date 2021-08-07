package com.objectcomputing.checkins.services.rale;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.rale.member.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class RaleServicesImpl implements RaleServices {

    private final RaleRepository ralesRepo;
    private final RaleMemberServices raleMemberServices;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public RaleServicesImpl(RaleRepository ralesRepo,
                            RaleMemberServices raleMemberServices,
                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices) {
        this.ralesRepo = ralesRepo;
        this.raleMemberServices = raleMemberServices;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    public RaleResponseDTO save(RaleCreateDTO raleDTO) {
        Rale newRaleEntity = null;
        List<RaleMemberResponseDTO> newMembers = new ArrayList<>();
        if (raleDTO != null) {
            if (!ralesRepo.search(raleDTO.getRale(), null).isEmpty()) {
                throw new BadArgException(String.format("Rale with name %s already exists", raleDTO.getRale()));
            } else {
                if (raleDTO.getRaleMembers() == null ||
                        raleDTO.getRaleMembers().stream().noneMatch(RaleCreateDTO.RaleMemberCreateDTO::getLead)) {
                    throw new BadArgException("Rale must include at least one rale lead");
                }
                newRaleEntity = ralesRepo.save(fromDTO(raleDTO));
                for (RaleCreateDTO.RaleMemberCreateDTO memberDTO : raleDTO.getRaleMembers()) {
                    MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                    newMembers.add(fromMemberEntity(raleMemberServices.save(fromMemberDTO(memberDTO, newRaleEntity.getId())), existingMember));
                }
            }
        }

        return fromEntity(newRaleEntity, newMembers);
    }

    public RaleResponseDTO read(@NotNull UUID raleId) {
        Rale foundRale = ralesRepo.findById(raleId)
                .orElseThrow(() -> new NotFoundException("No such rale found"));

        List<RaleMemberResponseDTO> raleMembers = raleMemberServices
                .findByFields(raleId, null, null)
                .stream()
                .filter(raleMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(raleMember.getMemberId()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(raleMember ->
                        fromMemberEntity(raleMember, memberProfileServices.getById(raleMember.getMemberId()))).collect(Collectors.toList());

        return fromEntity(foundRale, raleMembers);
    }

    public RaleResponseDTO update(RaleUpdateDTO raleDTO) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null &&
                !raleMemberServices.findByFields(raleDTO.getId(), currentUser.getId(), true).isEmpty())) {

            RaleResponseDTO updated = null;
            List<RaleMemberResponseDTO> newMembers = new ArrayList<>();
            if (raleDTO != null) {
                if (raleDTO.getId() != null && ralesRepo.findById(raleDTO.getId()).isPresent()) {
                    if (raleDTO.getRaleMembers() == null ||
                            raleDTO.getRaleMembers().stream().noneMatch(RaleUpdateDTO.RaleMemberUpdateDTO::getLead)) {
                        throw new BadArgException("Rale must include at least one rale lead");
                    }


                    Rale newRaleEntity = ralesRepo.update(fromDTO(raleDTO));

                    Set<RaleMember> existingRaleMembers = raleMemberServices.findByFields(raleDTO.getId(), null, null);
                    //add any new members & updates
                    raleDTO.getRaleMembers().stream().forEach((updatedMember) -> {
                        Optional<RaleMember> first = existingRaleMembers.stream().filter((existing) -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                        if (!first.isPresent()) {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(raleMemberServices.save(fromMemberDTO(updatedMember, newRaleEntity.getId())), existingMember));
                        } else {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(raleMemberServices.update(fromMemberDTO(updatedMember, newRaleEntity.getId())), existingMember));
                        }
                    });

                    //delete any removed members
                    existingRaleMembers.stream().forEach((existingMember) -> {
                        if (!raleDTO.getRaleMembers().stream().filter((updatedRaleMember) -> updatedRaleMember.getMemberId().equals(existingMember.getMemberId())).findFirst().isPresent()) {
                            raleMemberServices.delete(existingMember.getId());
                        }
                    });

                    updated = fromEntity(newRaleEntity, newMembers);
                } else {
                    throw new BadArgException(String.format("Rale ID %s does not exist, can't update.", raleDTO.getId()));
                }
            }
            return updated;
        } else {
            throw new PermissionException("You are not authorized to perform this operation");
        }
    }

    public Set<RaleResponseDTO> findByFields(RaleType rale, UUID memberid) {
        Set<RaleResponseDTO> foundRales = ralesRepo.search(rale, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (RaleResponseDTO foundRale : foundRales) {
            Set<RaleMember> foundMembers = raleMemberServices.findByFields(foundRale.getId(), null, null).stream().filter(raleMember -> {
                LocalDate terminationDate = memberProfileServices.getById(raleMember.getMemberId()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (RaleMember foundMember : foundMembers) {
                foundRale.getRaleMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberId())));
            }
        }
        return foundRales;
    }

    public boolean delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (isAdmin || (currentUser != null && !raleMemberServices.findByFields(id, currentUser.getId(), true).isEmpty())) {
            raleMemberServices.deleteByRale(id);
            ralesRepo.deleteById(id);
        } else {
            throw new PermissionException("You are not authorized to perform this operation");
        }
        return true;
    }

    private Rale fromDTO(RaleUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Rale(dto.getId(), dto.getRale(), dto.getDescription());
    }

    private RaleMember fromMemberDTO(RaleCreateDTO.RaleMemberCreateDTO memberDTO, UUID raleId) {
        return new RaleMember(null, raleId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private RaleMember fromMemberDTO(RaleUpdateDTO.RaleMemberUpdateDTO memberDTO, UUID raleId) {
        return new RaleMember(memberDTO.getId(), raleId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private RaleResponseDTO fromEntity(Rale entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private RaleResponseDTO fromEntity(Rale entity, List<RaleMemberResponseDTO> memberEntities) {
        if (entity == null) {
            return null;
        }
        RaleResponseDTO dto = new RaleResponseDTO(entity.getId(), entity.getRale(), entity.getDescription());
        dto.setRaleMembers(memberEntities);
        return dto;
    }

    private Rale fromDTO(RaleCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Rale(null, dto.getRale(), dto.getDescription());
    }

    private RaleMemberResponseDTO fromMemberEntity(RaleMember raleMember, MemberProfile memberProfile) {
        if (raleMember == null || memberProfile == null) {
            return null;
        }
        return new RaleMemberResponseDTO(raleMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), raleMember.isLead());
    }
}
