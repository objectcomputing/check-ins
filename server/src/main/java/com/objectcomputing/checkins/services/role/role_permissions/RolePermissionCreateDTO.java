package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Introspected
public class RolePermissionCreateDTO {

    @NotNull
    @Column(name = "roleid")
    private String roleId;

    @NotNull
    @Column(name = "permissionid")
    private String permissionId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
