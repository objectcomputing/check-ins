package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Setter
@Getter
@ToString
@Introspected
public class RolePermissionResponseDTO {

    @NotNull
    @Schema(description = "id of the role")
    private UUID roleId;

    @Schema(description = "the permission")
    private Permission permission;

    public RolePermissionResponseDTO(UUID roleId, Permission permission) {
        this.roleId = roleId;
        this.permission = permission;
    }

}
