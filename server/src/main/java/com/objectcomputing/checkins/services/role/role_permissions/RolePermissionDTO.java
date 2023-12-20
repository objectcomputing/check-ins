package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RolePermissionDTO {

    @NotNull
    @Schema(description = "id of the role", required = true)
    private UUID roleId;

    @NotNull
    @Schema(description = "id of the permission", required = true)
    private UUID permissionId;

    public RolePermissionDTO(UUID roleId, UUID permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RolePermissionCreateDTO{");
        sb.append("roleId=").append(roleId);
        sb.append(", permissionId=").append(permissionId);
        sb.append('}');
        return sb.toString();
    }
}
