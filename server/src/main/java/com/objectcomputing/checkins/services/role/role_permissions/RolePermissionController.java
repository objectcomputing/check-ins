package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
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
    public Mono<HttpResponse<List<RolePermissionsResponseDTO>>> getAllRolePermissions() {

        return Mono.fromCallable(rolePermissionServices::findAll)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(rolePermissions -> (HttpResponse<List<RolePermissionsResponseDTO>>) HttpResponse.ok(rolePermissions))
                .subscribeOn(scheduler);
    }

    @RequiredPermission(Permissions.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Post("/")
    public Mono<HttpResponse<RolePermissionDTO>> save(@Body @Valid RolePermissionDTO rolePermission) {
        return Mono.fromCallable(() -> rolePermissionServices.save(rolePermission.getRoleId(), rolePermission.getPermissionId()))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedRolePermission -> (HttpResponse<RolePermissionDTO>) HttpResponse
                        .created(fromEntity(savedRolePermission)))
                .subscribeOn(scheduler);
    }

    @RequiredPermission(Permissions.CAN_ASSIGN_ROLE_PERMISSIONS)
    @Delete("/")
    public Mono<HttpResponse> delete(@Body RolePermissionDTO dto) {

        return Mono.fromRunnable(() -> rolePermissionServices.delete(dto.getRoleId(), dto.getPermissionId()))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .subscribeOn(scheduler).thenReturn(HttpResponse.ok());
    }

    private RolePermissionDTO fromEntity(RolePermission rolePermission) {
        return new RolePermissionDTO(rolePermission.getRoleId(), rolePermission.getPermissionId());
    }
}
