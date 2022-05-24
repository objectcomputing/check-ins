package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "permission")
@Controller("/services/permissions")
public class PermissionController {

    private final PermissionServices permissionServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public PermissionController(PermissionServices permissionServices, EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService) {
        this.permissionServices = permissionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Get all permissions (orders by permission name)
     *
     * @return {@link List < Permission > list of Permissions}
     */
    @RequiredPermission(Permissions.CAN_VIEW_PERMISSIONS)
    @Get("/OrderByPermission")
    public Single<HttpResponse<List<Permission>>> listOrderByPermission() {

        return Single.fromCallable(permissionServices::listOrderByPermission)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get all permissions
     *
     * @return {@link List < Permission > list of Permissions}
     */
    @RequiredPermission(Permissions.CAN_VIEW_PERMISSIONS)
    @Get
    public Single<HttpResponse<List<Permission>>> getAllPermissions() {

        return Single.fromCallable(permissionServices::findAll)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    // TODO: Validate that the current user can only access this if memberId is theirs (or user is admin)
    /**
     * Get all permissions for a specific user
     * @param memberId the {@link UUID} of the member
     * @return list of permissions
     */
    @Get("/{memberId}")
    public Single<HttpResponse<List<Permission>>> getUserPermissions(UUID memberId) {
        return Single.fromCallable(() -> permissionServices.findUserPermissions(memberId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
