package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.member.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class RoleServicesImpl implements RoleServices {

    private final RoleRepository rolesRepo;
    private final RoleMemberServices roleMemberServices;
//    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public RoleServicesImpl(RoleRepository rolesRepo,
                            RoleMemberServices roleMemberServices,
//                            CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices) {
        this.rolesRepo = rolesRepo;
        this.roleMemberServices = roleMemberServices;
//        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    public RoleResponseDTO save(RoleCreateDTO roleDTO) {
        Role newRoleEntity = null;
        List<RoleMemberResponseDTO> newMembers = new ArrayList<>();
        if (roleDTO != null) {
            if (!rolesRepo.search(roleDTO.getRole(), null).isEmpty()) {
                throw new BadArgException(String.format("Role with name %s already exists", roleDTO.getRole()));
            } else {
                if (roleDTO.getRoleMembers() == null ||
                        roleDTO.getRoleMembers().stream().noneMatch(RoleCreateDTO.RoleMemberCreateDTO::getLead)) {
                    throw new BadArgException("Role must include at least one role lead");
                }
                newRoleEntity = rolesRepo.save(fromDTO(roleDTO));
                for (RoleCreateDTO.RoleMemberCreateDTO memberDTO : roleDTO.getRoleMembers()) {
                    MemberProfile existingMember = memberProfileServices.getById(memberDTO.getMemberId());
                    newMembers.add(fromMemberEntity(roleMemberServices.save(fromMemberDTO(memberDTO, newRoleEntity.getId())), existingMember));
                }
            }
        }

        return fromEntity(newRoleEntity, newMembers);
    }

    public RoleResponseDTO read(@NotNull UUID roleId) {
        Role foundRole = rolesRepo.findById(roleId)
                .orElseThrow(() -> new NotFoundException("No such role found"));

        List<RoleMemberResponseDTO> roleMembers = roleMemberServices
                .findByFields(roleId, null, null)
                .stream()
                .filter(roleMember -> {
                    LocalDate terminationDate = memberProfileServices.getById(roleMember.getMemberId()).getTerminationDate();
                    return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
                })
                .map(roleMember ->
                        fromMemberEntity(roleMember, memberProfileServices.getById(roleMember.getMemberId()))).collect(Collectors.toList());

        return fromEntity(foundRole, roleMembers);
    }

    public RoleResponseDTO update(RoleUpdateDTO roleDTO) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();

//        if (isAdmin || (currentUser != null &&
//                !roleMemberServices.findByFields(roleDTO.getId(), currentUser.getId(), true).isEmpty())) {

            RoleResponseDTO updated = null;
            List<RoleMemberResponseDTO> newMembers = new ArrayList<>();
            if (roleDTO != null) {
                if (roleDTO.getId() != null && rolesRepo.findById(roleDTO.getId()).isPresent()) {
                    if (roleDTO.getRoleMembers() == null ||
                            roleDTO.getRoleMembers().stream().noneMatch(RoleUpdateDTO.RoleMemberUpdateDTO::getLead)) {
                        throw new BadArgException("Role must include at least one role lead");
                    }


                    Role newRoleEntity = rolesRepo.update(fromDTO(roleDTO));

                    Set<RoleMember> existingRoleMembers = roleMemberServices.findByFields(roleDTO.getId(), null, null);
                    //add any new members & updates
                    roleDTO.getRoleMembers().stream().forEach((updatedMember) -> {
                        Optional<RoleMember> first = existingRoleMembers.stream().filter((existing) -> existing.getMemberId().equals(updatedMember.getMemberId())).findFirst();
                        if (!first.isPresent()) {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(roleMemberServices.save(fromMemberDTO(updatedMember, newRoleEntity.getId())), existingMember));
                        } else {
                            MemberProfile existingMember = memberProfileServices.getById(updatedMember.getMemberId());
                            newMembers.add(fromMemberEntity(roleMemberServices.update(fromMemberDTO(updatedMember, newRoleEntity.getId())), existingMember));
                        }
                    });

                    //delete any removed members
                    existingRoleMembers.stream().forEach((existingMember) -> {
                        if (!roleDTO.getRoleMembers().stream().filter((updatedRoleMember) -> updatedRoleMember.getMemberId().equals(existingMember.getMemberId())).findFirst().isPresent()) {
                            roleMemberServices.delete(existingMember.getId());
                        }
                    });

                    updated = fromEntity(newRoleEntity, newMembers);
                } else {
                    throw new BadArgException(String.format("Role ID %s does not exist, can't update.", roleDTO.getId()));
                }
            }
            return updated;
//        } else {
//            throw new PermissionException("You are not authorized to perform this operation");
//        }
    }

    public Set<RoleResponseDTO> findByFields(RoleType role, UUID memberid) {
        Set<RoleResponseDTO> foundRoles = rolesRepo.search(role, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (RoleResponseDTO foundRole : foundRoles) {
            Set<RoleMember> foundMembers = roleMemberServices.findByFields(foundRole.getId(), null, null).stream().filter(roleMember -> {
                LocalDate terminationDate = memberProfileServices.getById(roleMember.getMemberId()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (RoleMember foundMember : foundMembers) {
                foundRole.getRoleMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberId())));
            }
        }
        return foundRoles;
    }

    public boolean delete(@NotNull UUID id) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();

//        if (isAdmin || (currentUser != null && !roleMemberServices.findByFields(id, currentUser.getId(), true).isEmpty())) {
            roleMemberServices.deleteByRole(id);
            rolesRepo.deleteById(id);
//        } else {
//            throw new PermissionException("You are not authorized to perform this operation");
//        }
        return true;
    }

    @Override
    public List<RoleResponseDTO> findByRole(RoleType role) {
        return null;
    }

    @Override
    public List<RoleResponseDTO> findByMemberid(UUID uuid) {
        return null;
    }

    @Override
    public Optional<RoleResponseDTO> findByRoleAndMemberid(RoleType role, UUID memberId) {
        return Optional.empty();
    }

    @Override
    public void deleteByRoleAndMemberid(RoleType role, UUID memberId) {

    }

    private Role fromDTO(RoleUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Role(dto.getId(), dto.getRole(), dto.getDescription());
    }

    private RoleMember fromMemberDTO(RoleCreateDTO.RoleMemberCreateDTO memberDTO, UUID roleId) {
        return new RoleMember(null, roleId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private RoleMember fromMemberDTO(RoleUpdateDTO.RoleMemberUpdateDTO memberDTO, UUID roleId) {
        return new RoleMember(memberDTO.getId(), roleId, memberDTO.getMemberId(), memberDTO.getLead());
    }

    private RoleResponseDTO fromEntity(Role entity) {
        return fromEntity(entity, new ArrayList<>());
    }

    private RoleResponseDTO fromEntity(Role entity, List<RoleMemberResponseDTO> memberEntities) {
        if (entity == null) {
            return null;
        }
        RoleResponseDTO dto = new RoleResponseDTO(entity.getId(), entity.getRole(), entity.getDescription());
        dto.setRoleMembers(memberEntities);
        return dto;
    }

    private Role fromDTO(RoleCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Role(null, dto.getRole(), dto.getDescription());
    }

    private RoleMemberResponseDTO fromMemberEntity(RoleMember roleMember, MemberProfile memberProfile) {
        if (roleMember == null || memberProfile == null) {
            return null;
        }
        return new RoleMemberResponseDTO(roleMember.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), roleMember.isLead());
    }
}
