package com.objectcomputing.checkins.services.role.role_permissions;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    private final ExecutorService ioExecutorService;

    public RolePermissionController(RolePermissionServices rolePermissionServices, EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService) {
        this.rolePermissionServices = rolePermissionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Get all role permissions
     *
     * @return {@link List < RolePermission > list of RolePermission}
     */
    @RequiredPermission(Permissions.CAN_VIEW_ROLE_PERMISSIONS)
    @Get
    public Single<HttpResponse<List<RolePermissionResponseDTO>>> getAllRolePermissions() {

        return Single.fromCallable(rolePermissionServices::findAll)
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(rolePermissions -> (HttpResponse<List<RolePermissionResponseDTO>>) HttpResponse.ok(rolePermissions))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    @Post()
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<RolePermission> create(RolePermissionId id) {
        RolePermission rolePermission = rolePermissionServices.saveByIds(id.getRoleId(), id.getPermissionId());
        return HttpResponse.ok(rolePermission);
    }

    @Delete("/{roleId}/{permissionId}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    HttpResponse<?> delete(@NotNull UUID roleId, @NotNull UUID permissionId) {
        rolePermissionServices.delete(new RolePermissionId(roleId, permissionId));
        return HttpResponse.ok();
    }
}
