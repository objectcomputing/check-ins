package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileDoesNotExistException;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;

import static io.micronaut.http.HttpHeaders.CACHE_CONTROL;

@Controller("/services/member-profile/member-photo")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.IMAGE_PNG)
@Tag(name = "member photo")
public class MemberPhotoController {

    private final String expiry;
    private final MemberPhotoService memberPhotoService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public MemberPhotoController(@Property(name = "micronaut.caches.photo-cache.expire-after-write") String expiry,
                                 MemberPhotoService memberPhotoService,
                                 EventLoopGroup eventLoopGroup,
                                 @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.expiry = expiry;
        this.memberPhotoService = memberPhotoService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = MemberProfileDoesNotExistException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, MemberProfileDoesNotExistException e) {
        JsonError error = new JsonError(e.getMessage()).link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound().body(error);
    }

    /**
     * Get user photo data from Google Directory API
     *
     * @param workEmail
     * @return {@link HttpResponse<String>} StringURL of photo data
     */
    @Get("/{workEmail}")
    public Single<HttpResponse<byte[]>> userImage(@NotNull String workEmail) {

        System.out.println("Hit Controller!");

        return Single.fromCallable(() -> memberPhotoService.getImageByEmailAddress(workEmail))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(photoData -> (HttpResponse<byte[]>) HttpResponse
                        .ok(photoData)
                        .header(CACHE_CONTROL, String.format("public, max-age=%s", expiry)))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}
