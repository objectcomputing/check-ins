package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.PermissionDTO;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Introspected
public class RolePermissionsResponseDTO {

    @NotNull
    @Schema(description = "id of the role")
    private UUID roleId;

    @Nullable
    @Schema(description = "The name of the role")
    private String role;

    @Nullable
    @Schema(description = "The description of the role")
    private String description;

    @NotNull
    @Schema(description = "List of Permissions associated with this role")
    private List<PermissionDTO> permissions;

}
