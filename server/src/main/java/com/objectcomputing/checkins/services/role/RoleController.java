package com.objectcomputing.checkins.services.role;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/role")
@Secured(RoleType.Constants.ADMIN_ROLE)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "role")
public class RoleController {

    @Inject
    private RoleServices roleServices;

    @Error(exception = RoleBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, RoleBadArgException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest().body(error);
    }

    /**
     * Create and save a new role.
     *
     * @param role, {@link RoleCreateDTO}
     * @return {@link HttpResponse <Role>}
     */
    @Post()
    public HttpResponse<Role> create(@Body @Valid RoleCreateDTO role,
                                     HttpRequest<RoleCreateDTO> request) {
        Role newRole = roleServices.save(new Role(role.getRole(), role.getMemberid()));
        return HttpResponse
                .created(newRole)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newRole.getId()))));
    }

    /**
     * Update role.
     *
     * @param role, {@link Role}
     * @return {@link HttpResponse<Role>}
     */
    @Put()
    public HttpResponse<?> update(@Body @Valid Role role, HttpRequest<Role> request) {
        Role updatedRole = roleServices.update(role);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedRole.getId()))))
                .body(updatedRole);

    }

    /**
     * Get Role based off id
     *
     * @param id {@link RoleType} of the role member entry
     * @return {@link Role}
     */
    @Get("/{id}")
    public Role readRole(UUID id) {
        return roleServices.read(id);
    }

    /**
     * Find member roles that match all filled in parameters, return all results when given no params
     *
     * @param role     {@link RoleType} of role
     * @param memberid {@link UUID} of member
     * @return {@link List < Role > list of roles}
     */
    @Get("/{?role,memberid}")
    public Set<Role> findRole(@Nullable RoleType role,
                              @Nullable UUID memberid) {
        return roleServices.findByFields(role, memberid);
    }

    /**
     * Load roles
     *
     * @param roles, {@link List<RoleCreateDTO> to load {@link Role member roles}}
     * @return {@link HttpResponse<List<Role>}
     */
    @Post("/roles")
    public HttpResponse<?> loadRoles(@Body @Valid @NotNull List<RoleCreateDTO> roles,
                                     HttpRequest<List<Role>> request) {
        List<String> errors = new ArrayList<>();
        List<Role> rolesCreated = new ArrayList<>();
        for (RoleCreateDTO roleDTO : roles) {
            Role role = new Role(roleDTO.getRole(), roleDTO.getMemberid());
            try {
                roleServices.save(role);
                rolesCreated.add(role);
            } catch (RoleBadArgException e) {
                errors.add(String.format("Member %s was not given role %s because: %s", role.getMemberid(),
                        role.getRole(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(rolesCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }

    /**
     * Delete role
     *
     * @param id, id of {@link Role} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteRole(UUID id) {
        roleServices.delete(id);
        return HttpResponse
                .ok();
    }

}
