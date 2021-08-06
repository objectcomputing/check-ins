package com.objectcomputing.checkins.services.role_permissions;

import java.util.Set;
import java.util.UUID;

public interface RolePermissionServices {

    RolePermission save(RolePermission permission);

    RolePermission read(UUID id);

    RolePermission update(RolePermission permission);

    Set<RolePermission> findByFields(PermissionType permission, UUID roleid);

    void delete(UUID id);
}

