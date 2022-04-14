package com.objectcomputing.checkins.services.role.role_permissions;

import java.util.List;

public interface RolePermissionServices {

    RolePermission save(RolePermission rolePermission);

    List<RolePermissionResponseDTO> findAll();

}
