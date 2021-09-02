package com.objectcomputing.checkins.services.role_permissions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role_permissions.PermissionType.Constants.*;

@Introspected
public class RolePermissionCreateDTO {
    @NotNull
    @Column(name = "permission")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "role this member has", required = true,
            allowableValues = {READCHECKIN_PERMISSION, CREATECHECKIN_PERMISSION, DELETECHECKIN_PERMISSION, UNASSIGNED_PERMISSION})
    private PermissionType permission;

    @NotNull
    @Column(name = "roleid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the role this entry is associated with", required = true)
    private UUID roleid;

    public PermissionType getRolePermission() {
        return permission;
    }

    public void setRolePermission(PermissionType permission) {
        this.permission = permission;
    }

    public UUID getRoleid() {
        return roleid;
    }

    public void setRoleid(UUID roleid) {
        this.roleid = roleid;
    }
}

