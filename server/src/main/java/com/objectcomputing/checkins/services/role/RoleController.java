package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/services/roles")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "roles")
public class RoleController {

    private final RoleServices roleServices;

    public RoleController(RoleServices roleServices) {
        this.roleServices = roleServices;
    }

    /**
     * Create and save a new role.
     *
     * @param role, {@link RoleCreateDTO}
     * @return {@link HttpResponse <Role>}
     */
    @Post
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<Role>> create(@Body @Valid RoleCreateDTO role, HttpRequest<?> request) {
        return Mono.fromCallable(() -> roleServices.save(new Role(role.getRole(), role.getDescription())))
                .map(userRole -> HttpResponse.created(userRole)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), userRole.getId())))));
    }

    /**
     * Update role.
     *
     * @param role, {@link Role}
     * @return {@link HttpResponse<Role>}
     */
    @Put
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<Role>> update(@Body @Valid @NotNull Role role, HttpRequest<?> request) {
        return Mono.fromCallable(() -> roleServices.update(role))
                .map(updatedRole -> HttpResponse.ok(updatedRole)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedRole.getId())))));
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
        }).map(HttpResponse::ok);
    }



    /**
     * List all roles
     *
     * @return {@link Role}
     */
    @Get
    public Mono<HttpResponse<List<Role>>> findAll() {
        return Mono.fromCallable(roleServices::findAllRoles)
                .map(HttpResponse::ok);
    }

    /**
     * Delete role
     *
     * @param id, id of {@link Role} to delete
     */
    @Delete("/{id}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<?>> deleteRole(UUID id) {
        return Mono.fromRunnable(() -> roleServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

}