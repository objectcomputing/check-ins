package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.data.annotation.MappedEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import java.util.UUID;

@MappedEntity
@Table(name = "role_permissions")
public class RolePermission {

    @EmbeddedId
    private final RolePermissionId rolePermissionId;

    public RolePermission(RolePermissionId rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public RolePermission(UUID roleId, UUID permissionId) {
        this(new RolePermissionId(roleId, permissionId));
    }

    public RolePermissionId getRolePermissionId() {
        return rolePermissionId;
    }

    public UUID getRoleId() {
        return rolePermissionId.getRoleId();
    }

    public UUID getPermissionId() {
        return rolePermissionId.getPermissionId();
    }

}
