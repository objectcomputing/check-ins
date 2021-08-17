package com.objectcomputing.checkins.services.role.member;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class RoleMemberServicesImpl implements RoleMemberServices {

    private final RoleRepository roleRepo;
    private final RoleMemberRepository roleMemberRepo;
    private final MemberProfileRepository memberRepo;
//    private final CurrentUserServices currentUserServices;
    public RoleMemberServicesImpl(RoleRepository roleRepo,
                                  RoleMemberRepository roleMemberRepo,
                                  MemberProfileRepository memberRepo
//                                  CurrentUserServices currentUserServices
    ) {
        this.roleRepo = roleRepo;
        this.roleMemberRepo = roleMemberRepo;
        this.memberRepo = memberRepo;
//        this.currentUserServices = currentUserServices;
    }


    public RoleMember save(@Valid @NotNull RoleMember roleMember) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();
        final UUID roleId = roleMember.getRoleId();
        final UUID memberId = roleMember.getMemberId();

        Optional<Role> role = roleRepo.findById(roleId);
        if (role.isEmpty()) {
            throw new BadArgException(String.format("Role %s doesn't exist", roleId));
        }

        if (roleMember.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for role member", roleMember.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (roleMemberRepo.findByRoleIdAndMemberId(roleMember.getRoleId(), roleMember.getMemberId()).isPresent()) {
            throw new BadArgException(String.format("Member %s already exists in role %s", memberId, roleId));
//        } else if (!isAdmin) {
//            throw new BadArgException("You are not authorized to perform this operation");
        }

        RoleMember newRoleMember = roleMemberRepo.save(roleMember);
        return newRoleMember;
    }

    public RoleMember read(@NotNull UUID id) {
        return roleMemberRepo.findById(id).orElse(null);
    }

    public RoleMember update(@NotNull @Valid RoleMember roleMember) {

//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = roleMember.getId();
        final UUID roleId = roleMember.getRoleId();
        final UUID memberId = roleMember.getMemberId();
        Optional<Role> role = roleRepo.findById(roleId);

        if (role.isEmpty()) {
            throw new BadArgException(String.format("Role %s doesn't exist", roleId));
        }

        if (id == null || roleMemberRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate roleMember to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (roleMemberRepo.findByRoleIdAndMemberId(roleMember.getRoleId(), roleMember.getMemberId()).isEmpty()) {
            throw new BadArgException(String.format("Member %s is not part of role %s", memberId, roleId));
        }

        RoleMember roleMemberUpdate = roleMemberRepo.update(roleMember);
        return roleMemberUpdate;
    }

    public Set<RoleMember> findByFields(@Nullable UUID roleId, @Nullable UUID memberId) {
        Set<RoleMember> roleMembers = new HashSet<>();
        roleMemberRepo.findAll().forEach(roleMembers::add);

        if (roleId != null) {
            roleMembers.retainAll(roleMemberRepo.findByRoleId(roleId));
        }
        if (memberId != null) {
            roleMembers.retainAll(roleMemberRepo.findByMemberId(memberId));
        }

        return roleMembers;
    }

    public void delete(@NotNull UUID id) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();

        RoleMember roleMember = roleMemberRepo.findById(id).orElse(null);
        if (roleMember != null) {
            Set<RoleMember> roleLeads = this.findByFields(roleMember.getRoleId(), null);

//            if (!isAdmin && roleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
//                throw new PermissionException("You are not authorized to perform this operation");
//            } else {
//                roleMemberRepo.deleteById(id);
//            }
        } else {
            throw new NotFoundException(String.format("Unable to locate roleMember with id %s", id));
        }

        roleMemberRepo.delete(roleMember);
    }

    public void deleteByRole(@NotNull UUID id) {
//        MemberProfile currentUser = currentUserServices.getCurrentUser();
//        boolean isAdmin = currentUserServices.isAdmin();

        List<RoleMember> roleMembers = roleMemberRepo.findByRoleId(id);
        if (roleMembers != null) {

//            if (!isAdmin && roleLeads.stream().noneMatch(o -> o.getMemberId().equals(currentUser.getId()))) {
//                throw new PermissionException("You are not authorized to perform this operation");
//            } else {
//                roleMembers.forEach(member -> {
//                    roleMemberRepo.deleteById(member.getId());
//                });
//            }
        } else {
            throw new NotFoundException(String.format("Unable to locate role with id %s", id));
        }
    }
}