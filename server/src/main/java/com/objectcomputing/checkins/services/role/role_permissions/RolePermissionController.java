package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Delete;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Role Permissions")
@Controller("/services/roles/role-permissions")
public class RolePermissionController {

    private final RolePermissionServices rolePermissionServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public RolePermissionController(RolePermissionServices rolePermissionServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.rolePermissionServices = rolePermissionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Get all role permissions
     *
     * @return {@link List < RolePermission > list of RolePermission}
     */
    @RequiredPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS)
    @Get
    public Mono<HttpResponse<List<RolePermissionResponseDTO>>> getAllRolePermissions() {
        return Mono.fromCallable(rolePermissionServices::findAll)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(rolePermissions -> (HttpResponse<List<RolePermissionResponseDTO>>) HttpResponse.ok(rolePermissions))
                .subscribeOn(scheduler);
    }

    @Post
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<RolePermission>> saveRolePermission(@NotNull UUID roleId, @NotNull UUID permissionId) {
        return Mono.fromCallable(() -> rolePermissionServices.saveByIds(roleId, permissionId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(rolePermission -> (HttpResponse<RolePermission>) HttpResponse.created(rolePermission))
                .subscribeOn(scheduler);
    }

    @Delete("/{roleId}/{permissionId}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID roleId, @NotNull UUID permissionId) {
        return Mono.fromCallable(() -> rolePermissionServices.delete(new RolePermissionId(roleId, permissionId)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.noContent())
                .subscribeOn(scheduler);
    }
}
