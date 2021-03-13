package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/member-profile/current")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "current user")
public class CurrentUserController {

    private final CurrentUserServices currentUserServices;
    private final RoleRepository roleRepository;

    public CurrentUserController(CurrentUserServices currentUserServices,
                                 RoleRepository roleRepository) {
        this.currentUserServices = currentUserServices;
        this.roleRepository = roleRepository;
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
        String firstName = authentication.getAttributes().get("firstName").toString();
        String lastName = authentication.getAttributes().get("lastName").toString();
        String imageUrl = authentication.getAttributes().get("picture").toString();

        MemberProfile user = currentUserServices.findOrSaveUser(firstName, lastName, workEmail);
        List<String> roles = roleRepository.findByMemberid(user.getId()).stream().map(role -> role.getRole().toString()).collect(Collectors.toList());

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(user.getId())))
                .body(fromEntity(user, imageUrl, roles));
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-profile/" + uuid);
    }

    private CurrentUserDTO fromEntity(MemberProfile entity, String imageUrl, List<String> roles) {
        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setName(MemberProfileUtils.getFullName(entity));
        dto.setRole(roles);
        dto.setImageUrl(imageUrl);
        dto.setMemberProfile(entity);
        return dto;
    }
}
