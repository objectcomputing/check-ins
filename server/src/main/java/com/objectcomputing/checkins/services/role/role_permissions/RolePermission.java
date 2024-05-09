package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
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

    @Column(name = "permission")
    @TypeDef(type= DataType.STRING)
    private final Permission permission;

    public RolePermission(RolePermissionId rolePermissionId, UUID roleId, Permission permission) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permission = permission;
    }

    public RolePermission(UUID roleId, Permission permission) {
        this(null, roleId, permission);
    }

    public RolePermissionId getRolePermissionId() {
        return rolePermissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public Permission getPermission() {
        return permission;
    }
}
