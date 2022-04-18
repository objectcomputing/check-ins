package com.objectcomputing.checkins.services.role.role_permissions;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface RolePermissionServices {

    RolePermission saveByIds(UUID roleid, UUID permissionid);

    void delete(@NotNull RolePermissionId id);

    List<RolePermissionResponseDTO> findAll();

}
