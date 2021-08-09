package com.objectcomputing.checkins.services.role;


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

@Controller("/services/roles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "roles")
public class RoleController {

    private final RoleServices roleService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public RoleController(RoleServices roleService,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.roleService = roleService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new role
     *
     * @param role, {@link RoleCreateDTO}
     * @return {@link HttpResponse< RoleResponseDTO >}
     */
    @Post()
    public Single<HttpResponse<RoleResponseDTO>> createARole(@Body @Valid RoleCreateDTO role, HttpRequest<RoleCreateDTO> request) {

        return Single.fromCallable(() -> roleService.save(role))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdRole -> (HttpResponse<RoleResponseDTO>) HttpResponse
                        .created(createdRole)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdRole.getId())))))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get role based on id
     *
     * @param id of role
     * @return {@link RoleResponseDTO role matching id}
     */

    @Get("/{id}")
    public Single<HttpResponse<RoleResponseDTO>> readRole(@NotNull UUID id) {
        return Single.fromCallable(() -> roleService.read(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(role -> (HttpResponse<RoleResponseDTO>) HttpResponse.ok(role))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Find role(s) given a combination of the following parameters
     *
     * @param role,     name of the role
     * @param memberId, {@link UUID} of the member you wish to inquire in to which roles they are a part of
     * @return {@link List< RoleResponseDTO > list of roles}, return all roles when no parameters filled in else
     * return all roles that match all of the filled in params
     */
    @Get("/{?role,memberId}")
    public Single<HttpResponse<Set<RoleResponseDTO>>> findRoles(@Nullable RoleType role, @Nullable UUID memberId) {
        return Single.fromCallable(() -> roleService.findByFields(role, memberId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(roles -> (HttpResponse<Set<RoleResponseDTO>>) HttpResponse.ok(roles))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update role.
     *
     * @param role, {@link RoleUpdateDTO}
     * @return {@link HttpResponse< RoleResponseDTO >}
     */
    @Put()
    public Single<HttpResponse<RoleResponseDTO>> update(@Body @Valid RoleUpdateDTO role, HttpRequest<RoleUpdateDTO> request) {
        return Single.fromCallable(() -> roleService.update(role))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updated -> (HttpResponse<RoleResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), role.getId()))))
                        .body(updated))
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete Role
     *
     * @param id, id of {@link RoleUpdateDTO} to delete
     * @return
     */
    @Delete("/{id}")
    public Single<HttpResponse> deleteRole(@NotNull UUID id) {
        return Single.fromCallable(() -> roleService.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(success -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}