package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/member-profile/current")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "current user")
public class CurrentUserController {

    @Inject
    private CurrentUserServices currentUserServices;

    @Inject
    private RoleRepository roleRepository;

    /**
     * Get user details from Google authentication
     *
     * @param authentication {@link Authentication} or null
     * @return {@link HttpResponse<CurrentUserDTO>}
     */
    @Get
    public HttpResponse<CurrentUserDTO> currentUser(@Nullable Authentication authentication) {

        if (authentication == null) {
            return HttpResponse
                    .notFound();
        }

        String workEmail = authentication.getAttributes().get("email").toString();
        String name = authentication.getAttributes().get("name").toString();
        String imageUrl = authentication.getAttributes().get("picture").toString();

        MemberProfileEntity user = currentUserServices.findOrSaveUser(name, workEmail);
        List<String> roles = roleRepository.findByMemberid(user.getId()).stream().map(role -> role.getRole().toString()).collect(Collectors.toList());

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(user.getId())))
                .body(fromEntity(user, imageUrl, roles));
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-profile/" + uuid);
    }

    private CurrentUserDTO fromEntity(MemberProfileEntity entity, String imageUrl, List<String> roles) {
        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setName(entity.getName());
        dto.setRole(roles);
        dto.setImageUrl(imageUrl);
        dto.setMemberProfile(entity);
        return dto;
    }
}
