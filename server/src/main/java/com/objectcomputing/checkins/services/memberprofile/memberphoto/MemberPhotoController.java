package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;

import static io.micronaut.http.HttpHeaders.CACHE_CONTROL;

@Controller("/services/member-profiles/member-photos")
@Secured(SecurityRule.IS_AUTHENTICATED)
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

    /**
     * Get user photo data from Google Directory API
     *
     * @param workEmail
     * @return {@link HttpResponse<String>} StringURL of photo data
     */
    @Get("/{workEmail}")
    public Mono<HttpResponse<byte[]>> userImage(@NotNull String workEmail) {

        return Mono.fromCallable(() -> memberPhotoService.getImageByEmailAddress(workEmail))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(photoData -> (HttpResponse<byte[]>) HttpResponse
                        .ok(photoData)
                        .header(CACHE_CONTROL, String.format("public, max-age=%s", expiry)))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}
