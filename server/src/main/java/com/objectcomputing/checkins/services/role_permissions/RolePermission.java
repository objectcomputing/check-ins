package com.objectcomputing.checkins.services.role_permissions;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role_permissions.PermissionType.Constants.*;
import static com.objectcomputing.checkins.services.role_permissions.PermissionType.Constants.UNASSIGNED_PERMISSION;

@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this role to permission entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "permission this role has", required = true,
            allowableValues = {READCHECKIN_PERMISSION, CREATECHECKIN_PERMISSION, DELETECHECKIN_PERMISSION, UNASSIGNED_PERMISSION})
    @TypeDef(type = DataType.OBJECT)
    private PermissionType permission;

    @NotNull
    @Column(name = "roleid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the role this entry is associated with", required = true)
    private UUID roleid;

    public RolePermission(PermissionType permission, UUID roleid) {
        this(null, permission, roleid);
    }

    public RolePermission(UUID id, PermissionType permission, UUID roleid) {
        this.id = id;
        this.permission = permission;
        this.roleid = roleid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PermissionType getPermission() {
        return permission;
    }

    public void setPermission(PermissionType permission) {
        this.permission = permission;
    }

    public UUID getRoleid() {
        return roleid;
    }

    public void setRoleid(UUID roleid) {
        this.roleid = roleid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermission that = (RolePermission) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(roleid, that.roleid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permission, roleid);
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", permission=" + permission +
                ", roleid=" + roleid +
                '}';
    }
}
