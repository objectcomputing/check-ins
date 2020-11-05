package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.NotNull;

import static io.micronaut.http.HttpHeaders.CACHE_CONTROL;

@Controller("/services/member-profile/member-photo")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "member photo")
public class MemberPhotoController {

    private final String expiry;
    private final MemberPhotoService memberPhotoService;

    public MemberPhotoController(@Property(name = "ehcache.caches.photo-cache.expiry") String expiry,
                                 MemberPhotoService memberPhotoService) {
        this.expiry = expiry;
        this.memberPhotoService = memberPhotoService;
    }

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
                .header(CACHE_CONTROL, String.format("public, max-age=%s", expiry))
                .body(memberPhotoService.getImageByEmailAddress(workEmail));
    }
}
