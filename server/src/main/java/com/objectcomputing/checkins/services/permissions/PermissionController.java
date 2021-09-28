package com.objectcomputing.checkins.services.permissions;

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
    @Get
    public Single<HttpResponse<List<Permission>>> listOrderByPermission() {

        return Single.fromCallable(permissionServices::listOrderByPermission)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
