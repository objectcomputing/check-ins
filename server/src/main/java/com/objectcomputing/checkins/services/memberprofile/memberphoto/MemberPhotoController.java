package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import static io.micronaut.http.HttpHeaders.CACHE_CONTROL;

@Controller("/services/member-profiles/member-photos")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.IMAGE_PNG)
@Tag(name = "member photo")
public class MemberPhotoController {

    private final String expiry;
    private final MemberPhotoService memberPhotoService;

    public MemberPhotoController(@Property(name = "micronaut.caches.photo-cache.expire-after-write") String expiry,
                                 MemberPhotoService memberPhotoService) {
        this.expiry = expiry;
        this.memberPhotoService = memberPhotoService;
    }

    /**
     * Get user photo data from Google Directory API
     *
     * @param workEmail {@link String} work email of the user
     * @return StringURL of photo data
     */
    @Get("/{workEmail}")
    public HttpResponse<byte[]> userImage(@NotNull String workEmail) {
        byte[] photoData = memberPhotoService.getImageByEmailAddress(workEmail);
        return HttpResponse.ok(photoData)
                .header(CACHE_CONTROL, String.format("public, max-age=%s", expiry));
    }
}
