package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.PermissionType;

import java.util.UUID;

public interface RolePermissionFixture extends RepositoryFixture {
    default RolePermission createDefaultCheckinRolePermission(Role roleProfile) {
        return createDefaultRolePermission(PermissionType.CREATECHECKIN, roleProfile);
    }

    default RolePermission createDefaultRolePermission(PermissionType type, Role roleProfile) {
        return getRolePermissionRepository().save(new RolePermission(type, roleProfile.getId()));
    }

    default RolePermission findRolePermission(RolePermission permission) {
        return findRolePermissionById(permission.getId());
    }

    default RolePermission findRolePermissionById(UUID uuid) {
        return getRolePermissionRepository().findById(uuid).orElse(null);
    }

}
