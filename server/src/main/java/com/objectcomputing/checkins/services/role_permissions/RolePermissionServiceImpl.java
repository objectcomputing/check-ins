package com.objectcomputing.checkins.services.role_permissions;


import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.role.RoleRepository;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class RolePermissionServiceImpl implements RolePermissionServices {

    private final RolePermissionRepository rolepermissionRepo;
    private final RoleRepository roleRepo;

    public RolePermissionServiceImpl(RolePermissionRepository rolepermissionRepo,
                            RoleRepository roleRepo) {
        this.rolepermissionRepo = rolepermissionRepo;
        this.roleRepo = roleRepo;
    }

    public RolePermission save(@NotNull RolePermission permission) {
        final UUID memberId = permission.getRoleid();
        final RolePermissionType permissionType = permission.getPermission();
        final UUID roleId = permission.getRoleid();

        if (permissionType == null || roleId == null) {
            throw new BadArgException(String.format("Invalid permission %s", permission));
        } else if (permission.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for permission", permission.getId()));
        } else if (roleRepo.findById(roleId).isEmpty()) {
            throw new BadArgException(String.format("Role %s doesn't exist", roleId));
        } else if (rolepermissionRepo.findByPermissionAndRoleid(permissionType, permission.getRoleid()).isPresent()) {
            throw new BadArgException(String.format("Role Id %s already has permission %s", memberId, permissionType));
        }

        return rolepermissionRepo.save(permission);
    }

    public RolePermission read(@NotNull UUID id) {
        return rolepermissionRepo.findById(id).orElse(null);
    }

    public RolePermission update(@NotNull RolePermission permission) {
        final UUID id = permission.getId();
        final UUID roleId = permission.getRoleid();
        final RolePermissionType roleType = permission.getPermission();

        if (roleType == null || roleId == null) {
            throw new BadArgException(String.format("Invalid permission %s", permission));
        } else if (id == null || rolepermissionRepo.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Unable to locate permission to update with id %s", id));
        } else if (roleRepo.findById(roleId).isEmpty()) {
            throw new BadArgException(String.format("Role Id %s doesn't exist", roleId));
        }

        return rolepermissionRepo.update(permission);
    }

    public Set<RolePermission> findByFields(RolePermissionType permission, UUID roleid) {
        Set<RolePermission> permissions = new HashSet<>();
        rolepermissionRepo.findAll().forEach(permissions::add);

        if (permission != null) {
            permissions.retainAll(rolepermissionRepo.findByPermission(permission));
        }
        if (roleid != null) {
            permissions.retainAll(rolepermissionRepo.findByRoleid(roleid));
        }

        return permissions;
    }

    public void delete(@NotNull UUID id) {
        rolepermissionRepo.deleteById(id);
    }
}

