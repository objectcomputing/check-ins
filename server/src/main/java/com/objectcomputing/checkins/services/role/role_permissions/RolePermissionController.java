package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
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
    @RequiredPermission(Permission.CAN_VIEW_ROLE_PERMISSIONS)
    @Get
    public Mono<HttpResponse<List<RolePermissionsResponseDTO>>> getAllRolePermissions() {

        return Mono.fromCallable(rolePermissionServices::findAll)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(rolePermissions -> (HttpResponse<List<RolePermissionsResponseDTO>>) HttpResponse.ok(rolePermissions))
                .subscribeOn(scheduler);
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Post("/")
    public Mono<HttpResponse<RolePermissionResponseDTO>> save(@Body @Valid RolePermissionDTO rolePermission) {
        return Mono.fromCallable(() -> rolePermissionServices.save(rolePermission.getRoleId(), Permission.fromName(rolePermission.getPermission())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedRolePermission -> (HttpResponse<RolePermissionResponseDTO>) HttpResponse
                        .created(fromEntity(savedRolePermission)))
                .subscribeOn(scheduler);
    }

    @RequiredPermission(Permission.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Delete("/")
    public Mono<MutableHttpResponse<Object>> delete(@Body RolePermissionDTO dto) {

        return Mono.fromRunnable(() -> rolePermissionServices.delete(dto.getRoleId(), Permission.fromName(dto.getPermission())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .subscribeOn(scheduler).thenReturn(HttpResponse.ok());
    }

    private RolePermissionResponseDTO fromEntity(RolePermission rolePermission) {
        return new RolePermissionResponseDTO(rolePermission.getRoleId(), rolePermission.getPermission());
    }
}
