package com.objectcomputing.checkins.services.role.role_permissions;

import java.util.List;
import java.util.UUID;

public interface RolePermissionServices {
    List<RolePermissionsResponseDTO> findAll();

    RolePermission save(UUID roleId, UUID permissionId);

    void delete(UUID roleId, UUID permissionId);
}
