package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RolePermissionId {

    @Column(name="roleid")
    @TypeDef(type = DataType.STRING)
    private final UUID roleId;

    @Column(name = "permission")
    @TypeDef(type = DataType.STRING)
    private final String permission;

    public RolePermissionId(UUID roleId, String permission) {
        this.roleId = roleId;
        this.permission = permission;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permission);
    }
}
