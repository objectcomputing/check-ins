package com.objectcomputing.checkins.services.role_permissions;


import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionRepository;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionServices;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class RolePermissionServiceImpl implements RolePermissionServices {

    private final com.objectcomputing.checkins.services.role_permissions.RolePermissionRepository roleRepo;
    private final MemberProfileRepository memberRepo;

    public RolePermissionServiceImpl(RolePermissionRepository roleRepo,
                            MemberProfileRepository memberRepo) {
        this.roleRepo = roleRepo;
        this.memberRepo = memberRepo;
    }

    public RolePermission save(@NotNull RolePermission permission) {
        final UUID memberId = permission.getMemberid();
        final RolePermissionType permissionType = permission.getPermission();

        if (permissionType == null || memberId == null) {
            throw new BadArgException(String.format("Invalid permission %s", permission));
        } else if (permission.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for role", permission.getId()));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if (roleRepo.findByPermissionAndMemberid(permissionType, permission.getMemberid()).isPresent()) {
            throw new BadArgException(String.format("Member %s already has role %s", memberId, permissionType));
        }

        return roleRepo.save(permission);
    }

    public RolePermission read(@NotNull UUID id) {
        return roleRepo.findById(id).orElse(null);
    }

    public RolePermission update(@NotNull RolePermission permission) {
        final UUID id = permission.getId();
        final UUID memberId = permission.getMemberid();
        final RolePermissionType roleType = permission.getPermission();

        if (roleType == null || memberId == null) {
            throw new BadArgException(String.format("Invalid role %s", permission));
        } else if (id == null || roleRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate role to update with id %s", id));
        } else if (memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        }

        return roleRepo.update(permission);
    }

    public Set<RolePermission> findByFields(RolePermissionType permission, UUID memberid) {
        Set<RolePermission> permissions = new HashSet<>();
        roleRepo.findAll().forEach(permissions::add);

        if (permission != null) {
            permissions.retainAll(roleRepo.findByPermission(permission));
        }
        if (memberid != null) {
            permissions.retainAll(roleRepo.findByMemberid(memberid));
        }

        return permissions;
    }

    public void delete(@NotNull UUID id) {
        roleRepo.deleteById(id);
    }
}

