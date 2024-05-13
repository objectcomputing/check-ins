package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/member-profiles/current")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "current user")
public class CurrentUserController {

    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;

    private final RolePermissionServices rolePermissionServices;

    public CurrentUserController(CurrentUserServices currentUserServices, RoleServices roleServices, RolePermissionServices rolePermissionServices) {
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
        this.rolePermissionServices = rolePermissionServices;
    }

    /**
     * Get user details from Google authentication
     *
     * @param authentication {@link Authentication} or null
     * @return {@link HttpResponse<CurrentUserDTO>}
     */
    @Get
    public HttpResponse<CurrentUserDTO> currentUser(@Nullable Authentication authentication) {

        if (authentication == null) {
            return HttpResponse.unauthorized();
        }

        String workEmail = authentication.getAttributes().get("email").toString();
        String imageUrl = authentication.getAttributes().get("picture") != null ? authentication.getAttributes().get("picture").toString() : "";
        String name = authentication.getAttributes().get("name") != null ? authentication.getAttributes().get("name").toString().trim() : null;
        String firstName = name != null ? name.substring(0, name.indexOf(' ')) : "";
        String lastName = name != null ? name.substring(name.indexOf(' ') + 1).trim() : "";

        MemberProfile user = currentUserServices.findOrSaveUser(firstName, lastName, workEmail);
        List<Permission> permissions = rolePermissionServices.findUserPermissions(user.getId());

        Set<Role> roles = roleServices.findUserRoles(user.getId());
        List<String> rolesAsString = roles.stream().map(Role::getRole).collect(Collectors.toList());

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(user.getId())))
                .body(fromEntity(user, imageUrl, permissions, rolesAsString));
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-profiles/" + uuid);
    }

    private CurrentUserDTO fromEntity(MemberProfile entity, String imageUrl, List<Permission> permissions, List<String> roles) {
        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setName(MemberProfileUtils.getFullName(entity));
        dto.setPermissions(permissions);
        dto.setRole(roles);
        dto.setImageUrl(imageUrl);
        dto.setMemberProfile(entity);
        return dto;
    }
}
