package com.objectcomputing.checkins.services.permissions;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "permission")
@Controller("/services/permissions")
public class PermissionController {

    private final PermissionServices permissionServices;

    public PermissionController(PermissionServices permissionServices) {
        this.permissionServices = permissionServices;
    }

    /**
     * Get all permissions (orders by permission name)
     *
     * @return {@link List < Permission > list order by Permissions}
     */
    @Get("/OrderByPermission")
    @RequiredPermission(Permission.CAN_VIEW_PERMISSIONS)
    public List<Permission> listOrderByPermission() {
        return permissionServices.listOrderByPermission();
    }

    /**
     * Get all permissions
     *
     * @return {@link List < Permission > list of all Permissions}
     */
    @Get
    @RequiredPermission(Permission.CAN_VIEW_PERMISSIONS)
    public List<Permission> getAllPermissions() {
        return permissionServices.findAll();
    }
}
