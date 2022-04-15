package com.objectcomputing.checkins.services.role.role_permissions;

import java.util.List;
import java.util.UUID;

public interface RolePermissionServices {

    RolePermission saveByIds(UUID roleId, UUID permissionId);

    List<RolePermissionResponseDTO> findAll();

}
