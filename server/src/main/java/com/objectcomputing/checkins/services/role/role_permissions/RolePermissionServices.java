package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public interface RolePermissionServices {
  List<RolePermissionsResponseDTO> findAll();

  RolePermission save(UUID roleId, Permission permissionId);

  void delete(UUID roleId, Permission permissionId);

  List<RolePermission> findByRoleId(UUID roleId);

  List<RolePermission> findByRole(String role);

  List<Permission> findUserPermissions(@NotBlank UUID id);
}
