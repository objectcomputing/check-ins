package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
    private final Scheduler scheduler;

    public PermissionController(PermissionServices permissionServices,
                                EventLoopGroup eventLoopGroup,
                                @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.permissionServices = permissionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Get all permissions (orders by permission name)
     *
     * @return {@link List < Permission > list order by Permissions}
     */
    @RequiredPermission(Permissions.CAN_VIEW_PERMISSIONS)
    @Get("/OrderByPermission")
    public Mono<HttpResponse<List<Permission>>> listOrderByPermission() {

        return Mono.fromCallable(permissionServices::listOrderByPermission)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(scheduler);
    }

    /**
     * Get all permissions
     *
     * @return {@link List < Permission > list of all Permissions}
     */
    @RequiredPermission(Permissions.CAN_VIEW_PERMISSIONS)
    @Get
    public Mono<HttpResponse<List<Permission>>> getAllPermissions() {

        return Mono.fromCallable(permissionServices::findAll)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(permissions -> (HttpResponse<List<Permission>>) HttpResponse.ok(permissions))
                .subscribeOn(scheduler);
    }
}
