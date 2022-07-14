package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RolePermissionId {

    @TypeDef(type = DataType.STRING)
    @Column(name="roleid")
    private final UUID roleId;

    @TypeDef(type = DataType.STRING)
    @Column(name = "permissionid")
    private final UUID permissionId;

    public RolePermissionId(UUID roleId, UUID permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }
}
