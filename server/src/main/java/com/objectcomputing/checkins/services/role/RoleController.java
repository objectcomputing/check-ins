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
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<HttpResponse<Role>> create(@Body @Valid RoleCreateDTO role,
                                             HttpRequest<RoleCreateDTO> request) {
        return Mono.fromCallable(() -> roleServices.save(new Role(role.getRole(), role.getDescription())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(userRole -> {
                    return (HttpResponse<Role>) HttpResponse
                            .created(userRole)
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), userRole.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update role.
     *
     * @param role, {@link Role}
     * @return {@link HttpResponse<Role>}
     */
    @Put()
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<Role>> update(@Body @Valid @NotNull Role role, HttpRequest<Role> request) {
        return Mono.fromCallable(() -> roleServices.update(role))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedRole -> (HttpResponse<Role>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedRole.getId()))))
                        .body(updatedRole))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Get Role based off id
     *
     * @param id {@link RoleType} of the role member entry
     * @return {@link Role}
     */
    @Get("/{id}")
    public Mono<HttpResponse<Role>> readRole(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            Role result = roleServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup)).map(userRole -> {
                    return (HttpResponse<Role>) HttpResponse.ok(userRole);
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }



    /**
     * List all roles
     *
     * @return {@link Role}
     */
    @Get()
    public Mono<HttpResponse<List<Role>>> findAll() {
        return Mono.fromCallable(() -> {
            List<Role> result = roleServices.findAllRoles();
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup)).map(userRole -> {
            return (HttpResponse<List<Role>>) HttpResponse.ok(userRole);
        }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
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