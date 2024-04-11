package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.permissions.Permission;

import java.util.UUID;

public interface RolePermissionFixture extends RepositoryFixture {
    default void setRolePermission(UUID roleId, Permission permission) {
        getRolePermissionRepository().saveByIds(roleId.toString(), permission);
    }
}
