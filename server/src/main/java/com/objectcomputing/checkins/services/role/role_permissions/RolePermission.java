package com.objectcomputing.checkins.services.role.role_permissions;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import java.util.UUID;

@MappedEntity
@Table(name = "role_permissions")
public class RolePermission {

    @EmbeddedId
    private final RolePermissionId rolePermissionId;

    @Column(name="roleid")
    @TypeDef(type= DataType.STRING)
    private final UUID roleId;

    @Column(name = "permissionid")
    @TypeDef(type= DataType.STRING)
    private final UUID permissionId;

    public RolePermission(RolePermissionId rolePermissionId, UUID roleId, UUID permissionId) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public RolePermission(UUID roleId, UUID permissionId) {
        this(null, roleId, permissionId);
    }

    public RolePermissionId getRolePermissionId() {
        return rolePermissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }
}
