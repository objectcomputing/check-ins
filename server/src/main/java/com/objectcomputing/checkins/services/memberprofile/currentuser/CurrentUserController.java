package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
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
import java.util.*;

@Controller("/services/member-profile/current")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "current user")
public class CurrentUserController {

    @Inject
    CurrentUserServices currentUserServices;

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

        MemberProfile user = currentUserServices.findOrSaveUser(name, workEmail);

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(user.getUuid())))
                .body(fromEntity(user, imageUrl));
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/member-profile/" + uuid);
    }

    private CurrentUserDTO fromEntity(MemberProfile entity, String imageUrl) {
        CurrentUserDTO dto = new CurrentUserDTO();
        dto.setId(entity.getUuid());
        dto.setName(entity.getName());
        dto.setRole(entity.getRole());
        dto.setPdlId(entity.getPdlId());
        dto.setLocation(entity.getLocation());
        dto.setWorkEmail(entity.getWorkEmail());
        dto.setInsperityId(entity.getInsperityId());
        dto.setStartDate(entity.getStartDate());
        dto.setBioText(entity.getBioText());
        dto.setImageUrl(imageUrl);
        return dto;
    }
}
