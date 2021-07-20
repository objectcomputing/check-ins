package com.objectcomputing.checkins.services.role_permissions;


import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionCreateDTO;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionServices;
import com.objectcomputing.checkins.services.role_permissions.RolePermissionType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/permissions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "permissions")
public class RolePermissionController {

    private final RolePermissionServices roleServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public RolePermissionController(RolePermissionServices roleServices,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.roleServices = roleServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new role.
     *
     * @param permission, {@link RolePermissionCreateDTO}
     * @return {@link HttpResponse <RolePermission>}
     */

    @Post()
    @Secured(RolePermissionType.Constants.ADMIN_ROLE)
    public Single<HttpResponse<RolePermission>> create(@Body @Valid RolePermissionCreateDTO permission,
                                             HttpRequest<RolePermissionCreateDTO> request) {
        return Single.fromCallable(() -> roleServices.save(new RolePermission(permission.getRole(), permission.getMemberid())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(userRole -> {
                    return (HttpResponse<RolePermission>) HttpResponse
                            .created(userRole)
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), userRole.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update role.
     *
     * @param permission, {@link RolePermission}
     * @return {@link HttpResponse<RolePermission>}
     */
    @Put()
    @Secured(RolePermissionType.Constants.ADMIN_ROLE)
    public Single<HttpResponse<RolePermission>> update(@Body @Valid @NotNull RolePermission permission, HttpRequest<RolePermission> request) {
        return Single.fromCallable(() -> roleServices.update(permission))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedRole -> (HttpResponse<RolePermission>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedRole.getId()))))
                        .body(updatedRole))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get Role based off id
     *
     * @param id {@link RolePermissionType} of the role member entry
     * @return {@link RolePermission}
     */
    @Get("/{id}")
    public Single<HttpResponse<RolePermission>> readRole(@NotNull UUID id) {
        return Single.fromCallable(() -> {
            RolePermission result = roleServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        }).observeOn(Schedulers.from(eventLoopGroup)).map(userRole -> {
            return (HttpResponse<RolePermission>) HttpResponse.ok(userRole);
        }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find member roles that match all filled in parameters, return all results when given no params
     *
     * @param permission     {@link RolePermissionType} of role
     * @param memberid {@link UUID} of member
     * @return {@link List < RolePermission > list of permissions}
     */
    @Get("/{?permission,memberid}")
    public Single<HttpResponse<Set<RolePermission>>> findRole(@Nullable RolePermissionType permission, @Nullable UUID memberid) {
        return Single.fromCallable(() -> roleServices.findByFields(permission, memberid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(userRole -> (HttpResponse<Set<RolePermission>>) HttpResponse.ok(userRole))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete permission
     *
     * @param id, id of {@link RolePermission} to delete
     */
    @Delete("/{id}")
    @Secured(RolePermissionType.Constants.ADMIN_ROLE)
    public HttpResponse<?> deleteRole(UUID id) {
        roleServices.delete(id);
        return HttpResponse.ok();
    }

}