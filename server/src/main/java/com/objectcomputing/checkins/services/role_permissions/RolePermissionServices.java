package com.objectcomputing.checkins.services.role_permissions;

import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;

import java.util.Set;
import java.util.UUID;

public interface RolePermissionServices {

    RolePermission save(RolePermission permission);

    RolePermission read(UUID id);

    RolePermission update(RolePermission permission);

    Set<RolePermission> findByFields(RolePermissionType permission, UUID memberid);

    void delete(UUID id);
}

