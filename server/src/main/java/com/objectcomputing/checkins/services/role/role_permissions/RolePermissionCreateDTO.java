package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class RolePermissionCreateDTO {

    @NotNull
    @Column(name = "roleid")
    private UUID roleId;

    @NotNull
    @Column(name = "permissionid")
    private UUID permissionId;

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
}
