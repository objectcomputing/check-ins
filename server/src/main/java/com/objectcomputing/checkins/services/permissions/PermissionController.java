package com.objectcomputing.checkins.services.permissions;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

import java.util.List;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
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
    @RequiredPermission(Permission.CAN_VIEW_PERMISSIONS)
    @Get("/OrderByPermission")
    public Mono<HttpResponse<List<Permission>>> listOrderByPermission() {
        return Mono.fromCallable(permissionServices::listOrderByPermission)
                .map(HttpResponse::ok);
    }

    /**
     * Get all permissions
     *
     * @return {@link List < Permission > list of all Permissions}
     */
    @RequiredPermission(Permission.CAN_VIEW_PERMISSIONS)
    @Get
    public Mono<HttpResponse<List<Permission>>> getAllPermissions() {
        return Mono.fromCallable(permissionServices::findAll)
                .map(HttpResponse::ok);
    }
}
