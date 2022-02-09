package com.objectcomputing.checkins.services.calendar;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.google.api.services.calendar.model.Event;

import java.io.File;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Validated
@Controller("/services/calendar")
@Tag(name = "calendar")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GoogleCalendarController {

    private final GoogleCalendarServices googleCalendarServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public GoogleCalendarController(GoogleCalendarServices googleCalendarServices,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.googleCalendarServices = googleCalendarServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

        /**
     * Uploads event to Google Calendar
     *
     *
     */

    @Post("/")
    public Single<HttpResponse<String>> save() {
        return Single.fromCallable(() -> googleCalendarServices.save())
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(savedEvent -> (HttpResponse<String>) HttpResponse
        .ok()
        .headers(headers -> headers.location(URI.create("/event")))
        .body(savedEvent))
        .subscribeOn(Schedulers.from(ioExecutorService));
    }



    
}
