package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
//import com.objectcomputing.checkins.services.role.RoleUpdateDTO;
import com.objectcomputing.checkins.services.role.member.RoleMember;
import com.objectcomputing.checkins.services.role.member.RoleMemberRepository;
import com.objectcomputing.checkins.services.role.member.RoleMemberResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class RoleServicesImpl implements RoleServices {

    private final RoleRepository roleRepo;
    private final MemberProfileRepository memberRepo;
    private final RoleMemberRepository rolememberRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public RoleServicesImpl(RoleRepository roleRepo,
                            MemberProfileRepository memberRepo, RoleMemberRepository rolememberRepo, CurrentUserServices currentUserServices,
                            MemberProfileServices memberProfileServices) {
        this.roleRepo = roleRepo;
        this.memberRepo = memberRepo;
        this.rolememberRepo = rolememberRepo;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    public Role save(@NotNull Role role) {
        final RoleType roleType = role.getRole();

        if (roleType == null) {
            throw new BadArgException(String.format("Invalid role %s", role));
        } else if (role.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for role", role.getId()));
        } else if (!roleRepo.findByRole(roleType).isEmpty()) {
            throw new BadArgException(String.format("already has role %s", roleType));
        }

        return roleRepo.save(role);
    }

    public Role read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public Role update(@NotNull Role role) {
        final UUID id = role.getId();
        final RoleType roleType = role.getRole();

        if (roleType == null) {
            throw new BadArgException(String.format("Invalid role %s", role));
        } else if (id == null || roleRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate role to update with id %s", id));
        }

        return roleRepo.update(role);
    }

    public Set<Role> findByFields(RoleType role) {
        Set<Role> roles = new HashSet<>();
        roleRepo.findAll().forEach(roles::add);

        if (role != null) {
            roles.retainAll(roleRepo.findByRole(role));
        }

        return roles;
    }

    @Override
    public Set<RoleResponseDTO> findByMemberid(UUID memberid) {
        Set<RoleResponseDTO> foundRoles = roleRepo.search(null, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (RoleResponseDTO foundRole : foundRoles) {
            Set<RoleMember> foundMembers = rolememberRepo.findByRoleid(foundRole.getId()).stream().filter(roleMember -> {
                LocalDate terminationDate = memberProfileServices.getById(roleMember.getMemberid()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (RoleMember foundMember : foundMembers) {
                foundRole.getRoleMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberid())));
            }
        }
        return foundRoles;
//        return null;
    }

    public void delete(@NotNull UUID id) {
        roleRepo.deleteById(id);
    }

//    public Set<RoleResponseDTO> findByMemberId(UUID memberid) {
//        Set<RoleResponseDTO> foundRoles = roleRepo.search(null, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
//        //TODO: revisit this in a way that will allow joins.
//        for (RoleResponseDTO foundRole : foundRoles) {
//            Set<RoleMember> foundMembers = rolememberRepo.findByRoleid(foundRole.getId()).stream().filter(roleMember -> {
//                LocalDate terminationDate = memberProfileServices.getById(roleMember.getMemberid()).getTerminationDate();
//                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
//            }).collect(Collectors.toSet());
//
//            for (RoleMember foundMember : foundMembers) {
//                foundRole.getRoleMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberid())));
//            }
//        }
//        return foundRoles;
//    }




    public Set<RoleResponseDTO> findByRoleAndMemberid(RoleType role, UUID memberid) {
        Set<RoleResponseDTO> foundRoles = roleRepo.search(role, nullSafeUUIDToString(memberid)).stream().map(this::fromEntity).collect(Collectors.toSet());
        //TODO: revisit this in a way that will allow joins.
        for (RoleResponseDTO foundRole : foundRoles) {
            Set<RoleMember> foundMembers = rolememberRepo.findByRoleid(foundRole.getId()).stream().filter(roleMember -> {
                LocalDate terminationDate = memberProfileServices.getById(roleMember.getMemberid()).getTerminationDate();
                return terminationDate == null || !LocalDate.now().plusDays(1).isAfter(terminationDate);
            }).collect(Collectors.toSet());

            for (RoleMember foundMember : foundMembers) {
                foundRole.getRoleMembers().add(fromMemberEntity(foundMember, memberProfileServices.getById(foundMember.getMemberid())));
            }
        }
        return foundRoles;
    }

//    public boolean delete(@NotNull UUID id) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();
//
//        if (isAdmin || (currentUser != null && !rolememberRepo.search(nullSafeUUIDToString(id), nullSafeUUIDToString(currentUser.getId())).isEmpty())) {
//            rolememberRepo.deleteByRoleId(id.toString());
//            roleRepo.deleteById(id);
//        } else {
//            throw new PermissionException("You are not authorized to perform this operation");
//        }
//        return true;
//    }

//    private Role fromDTO(RoleUpdateDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//        return new Role(dto.getId(), dto.getRole(), dto.getDescription());
//    }

//    private RoleMember fromMemberDTO(RoleCreateDTO.RoleMemberCreateDTO memberDTO, UUID roleId) {
//        return new RoleMember(null, roleId, memberDTO.getMemberId());
//    }

    private RoleMember fromMemberDTO(RoleMemberResponseDTO memberDTO, UUID roleId, MemberProfile savedMember) {
        return new RoleMember(memberDTO.getId() == null ? null : memberDTO.getId(), roleId, savedMember.getId());
    }

    private RoleMember fromMemberDTO(RoleUpdateDTO.RoleMemberUpdateDTO memberDTO, UUID roleId) {
        return new RoleMember(memberDTO.getId(), roleId, memberDTO.getMemberId());
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
                memberProfile.getId());
    }
}
