package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/roles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "roles")
public class RoleController {

    private final RoleServices roleServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public RoleController(RoleServices roleServices,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.roleServices = roleServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new role.
     *
     * @param role, {@link RoleCreateDTO}
     * @return {@link HttpResponse <Role>}
     */

    @Post()
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Single<HttpResponse<Role>> create(@Body @Valid RoleCreateDTO role,
                                             HttpRequest<RoleCreateDTO> request) {
        return Single.fromCallable(() -> roleServices.save(new Role(role.getRole(), role.getMemberid())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(userRole -> {
                    return (HttpResponse<Role>) HttpResponse
                            .created(userRole)
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), userRole.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update role.
     *
     * @param role, {@link Role}
     * @return {@link HttpResponse<Role>}
     */
    @Put()
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Single<HttpResponse<Role>> update(@Body @Valid @NotNull Role role, HttpRequest<Role> request) {
        return Single.fromCallable(() -> roleServices.update(role))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedRole -> (HttpResponse<Role>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedRole.getId()))))
                        .body(updatedRole))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get Role based off id
     *
     * @param id {@link RoleType} of the role member entry
     * @return {@link Role}
     */
    @Get("/{id}")
    public Single<HttpResponse<Role>> readRole(@NotNull UUID id) {
        return Single.fromCallable(() -> {
            Role result = roleServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        }).observeOn(Schedulers.from(eventLoopGroup)).map(userRole -> {
                    return (HttpResponse<Role>) HttpResponse.ok(userRole);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find member roles that match all filled in parameters, return all results when given no params
     *
     * @param role     {@link RoleType} of role
     * @param memberid {@link UUID} of member
     * @return {@link List < Role > list of roles}
     */
    @Get("/{?role,memberid}")
    public Single<HttpResponse<Set<Role>>> findRole(@Nullable RoleType role, @Nullable UUID memberid) {
        return Single.fromCallable(() -> roleServices.findByFields(role, memberid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(userRole -> (HttpResponse<Set<Role>>) HttpResponse.ok(userRole))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete role
     *
     * @param id, id of {@link Role} to delete
     */
    @Delete("/{id}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> deleteRole(UUID id) {
        roleServices.delete(id);
        return HttpResponse.ok();
    }

}