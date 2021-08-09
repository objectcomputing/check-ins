package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.RoleServices;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/member-profiles/current")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "current user")
public class CurrentUserController {

    private final CurrentUserServices currentUserServices;
    private final RoleRepository roleRepository;
    private final RoleServices roleServices;

    public CurrentUserController(CurrentUserServices currentUserServices,
                                 RoleRepository roleRepository,
                                 RoleServices roleServices) {
        this.currentUserServices = currentUserServices;
        this.roleRepository = roleRepository;
        this.roleServices = roleServices;
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
        String name = authentication.getAttributes().get("name").toString().trim();
        String firstName = name.substring(0, name.indexOf(' '));
        String lastName = name.substring(name.indexOf(' ') + 1).trim();

        MemberProfile user = currentUserServices.findOrSaveUser(firstName, lastName, workEmail);
        List<String> roles = roleServices.findByMemberid(user.getId()).stream()
                .map(role -> role.getRole().toString()).collect(Collectors.toList());

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(user.getId())))
                .body(fromEntity(user, imageUrl, roles));
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-profiles/" + uuid);
    }

    private CurrentUserDTO fromEntity(MemberProfile entity, String imageUrl, List<String> roles) {
        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setName(MemberProfileUtils.getFullName(entity));
        dto.setRole(roles);
        dto.setImageUrl(imageUrl);
        dto.setMemberProfile(entity);
        return dto;
    }
}
