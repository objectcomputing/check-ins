package com.objectcomputing.checkins.services.role.member;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.member.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class RoleMemberServicesImpl implements RoleMemberServices {

    private final RoleRepository roleRepo;
    private final RoleMemberRepository roleMemberRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;

    public RoleMemberServicesImpl(RoleRepository roleRepo,
                                   RoleMemberRepository roleMemberRepo,
                                   MemberProfileRepository memberRepo,
                                   CurrentUserServices currentUserServices) {
        this.roleRepo = roleRepo;
        this.roleMemberRepo = roleMemberRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }

    public RoleMember save(@Valid @NotNull RoleMember roleMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID roleId = roleMember.getRoleid();
        final UUID memberId = roleMember.getMemberid();
        Optional<Role> role = roleRepo.findById(roleId);
        if (role.isEmpty()) {
            throw new BadArgException(String.format("Role %s doesn't exist", roleId));
        }


        if (roleMember.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for Role member", roleMember.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (roleMemberRepo.findByRoleidAndMemberid(roleMember.getRoleid(), roleMember.getMemberid()).isPresent()) {
            throw new BadArgException(String.format("Member %s already exists in role %s", memberId, roleId));
        }
        RoleMember roleMemberSaved = roleMemberRepo.save(roleMember);
        return roleMemberSaved;
    }

    public RoleMember read(@NotNull UUID id) {
        return roleMemberRepo.findById(id).orElse(null);
    }

    public RoleMember update(@NotNull @Valid RoleMember roleMember) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID id = roleMember.getId();
        final UUID roleId = roleMember.getRoleid();
        final UUID memberId = roleMember.getMemberid();
        Optional<Role> role = roleRepo.findById(roleId);

        if (role.isEmpty()) {
            throw new BadArgException(String.format("Role %s doesn't exist", roleId));
        }


        if (id == null || roleMemberRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate roleMember to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (roleMemberRepo.findByRoleidAndMemberid(roleMember.getRoleid(), roleMember.getMemberid()).isEmpty()) {
            throw new BadArgException(String.format("Member %s is not part of role %s", memberId, roleId));
        }
        RoleMember roleMemberUpdate = roleMemberRepo.update(roleMember);
        return roleMemberUpdate;
    }

    public Set<RoleMember> findByFields(@Nullable UUID roleid, @Nullable UUID memberid) {
        Set<RoleMember> roleMembers = new HashSet<>();
        roleMemberRepo.findAll().forEach(roleMembers::add);

        if (roleid != null) {
            roleMembers.retainAll(roleMemberRepo.findByRoleid(roleid));
        }
        if (memberid != null) {
            roleMembers.retainAll(roleMemberRepo.findByMemberid(memberid));
        }

        return roleMembers;
    }

    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        RoleMember roleMember = roleMemberRepo.findById(id).orElse(null);
        if (roleMember != null) {
                roleMemberRepo.deleteById(id);
            }
         else {
            throw new NotFoundException(String.format("Unable to locate roleMember with id %s", id));
        }

    }
}
