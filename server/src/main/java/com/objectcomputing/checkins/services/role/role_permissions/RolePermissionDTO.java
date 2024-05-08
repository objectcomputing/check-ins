package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class RolePermissionDTO {

    @NotNull
    @Schema(description = "id of the role", required = true)
    private UUID roleId;

    @Schema(description = "the permission", required = true)
    private String permission;

    public RolePermissionDTO(UUID roleId, Permission permission) {
        this.roleId = roleId;
        this.permission = permission.name();
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RolePermissionCreateDTO{");
        sb.append("roleId=").append(roleId);
        sb.append(", permission=").append(permission);
        sb.append('}');
        return sb.toString();
    }
}
