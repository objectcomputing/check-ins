package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public List<RolePermissionsResponseDTO> getAllRolePermissions() {
        return rolePermissionServices.findAll();
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Post
    @Status(HttpStatus.CREATED)
    public HttpResponse<RolePermissionResponseDTO> save(@Body @Valid RolePermissionDTO rolePermission) {
        RolePermission savedRolePermission = rolePermissionServices.save(rolePermission.getRoleId(), Permission.fromName(rolePermission.getPermission()));
        return HttpResponse.created(fromEntity(savedRolePermission));
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Delete("/")
    @Status(HttpStatus.OK)
    public void delete(@Body RolePermissionDTO dto) {
        rolePermissionServices.delete(dto.getRoleId(), Permission.fromName(dto.getPermission()));
    }

    private RolePermissionResponseDTO fromEntity(RolePermission rolePermission) {
        return new RolePermissionResponseDTO(rolePermission.getRoleId(), rolePermission.getPermission());
    }
}
