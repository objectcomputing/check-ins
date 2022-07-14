package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Introspected
public class RolePermissionResponseDTO {

    @NotNull
    @Schema(description = "id of the role", required = true)
    private UUID roleId;

    @NotNull
    @Schema(description = "The name of the role", required = true)
    private String role;

    @Nullable
    @Schema(description = "The description of the role")
    private String description;

    @NotNull
    @Schema(description = "List of Permissions associated with this role", required = true)
    private List<Permission> permissions;

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "RolePermissionResponseDTO{" +
                "roleId=" + roleId +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
