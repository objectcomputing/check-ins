package com.objectcomputing.checkins.services.fixture;

import java.util.UUID;

public interface RolePermissionFixture extends RepositoryFixture {
    default void setRolePermission(UUID roleId, UUID permissionId) {
        getRolePermissionRepository().saveByIds(roleId.toString(), permissionId.toString());
    }
}
