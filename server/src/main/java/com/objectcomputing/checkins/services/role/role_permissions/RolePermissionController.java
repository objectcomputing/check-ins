package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Role Permissions")
@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/services/roles/role-permissions")
public class RolePermissionController {

    private final RolePermissionServices rolePermissionServices;

    public RolePermissionController(RolePermissionServices rolePermissionServices) {
        this.rolePermissionServices = rolePermissionServices;
    }

    /**
     * Get all role permissions
     *
     * @return {@link List < RolePermission > list of RolePermission}
     */
    @RequiredPermission(Permission.CAN_VIEW_ROLE_PERMISSIONS)
    @Get
    public Mono<HttpResponse<List<RolePermissionsResponseDTO>>> getAllRolePermissions() {
        return Mono.fromCallable(rolePermissionServices::findAll)
                .map(HttpResponse::ok);
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Post()
    public Mono<HttpResponse<RolePermissionResponseDTO>> save(@Body @Valid RolePermissionDTO rolePermission) {
        return Mono.fromCallable(() -> rolePermissionServices.save(rolePermission.getRoleId(), Permission.fromName(rolePermission.getPermission())))
                .map(savedRolePermission -> HttpResponse.created(fromEntity(savedRolePermission)));
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Delete("/")
    public Mono<MutableHttpResponse<Object>> delete(@Body RolePermissionDTO dto) {
        return Mono.fromRunnable(() -> rolePermissionServices.delete(dto.getRoleId(), Permission.fromName(dto.getPermission())))
                .thenReturn(HttpResponse.ok());
    }

    private RolePermissionResponseDTO fromEntity(RolePermission rolePermission) {
        return new RolePermissionResponseDTO(rolePermission.getRoleId(), rolePermission.getPermission());
    }
}
