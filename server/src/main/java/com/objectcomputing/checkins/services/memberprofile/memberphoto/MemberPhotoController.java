package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@Controller("/services/member-profile/member-photo")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "member photo")
public class MemberPhotoController {

    @Inject
    private MemberPhotoService memberPhotoService;

    /**
     * Get user photo data from Google Directory API
     *
     * @param workEmail
     * @return {@link HttpResponse<String>} StringURL of photo data
     */
    @Get
    public HttpResponse<String> userImage(@NotNull String workEmail) {
        return HttpResponse
                .ok()
                .body(memberPhotoService.getImageByEmailAddress(workEmail));
    }
}
