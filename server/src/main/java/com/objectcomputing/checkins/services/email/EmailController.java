package com.objectcomputing.checkins.services.email;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Controller("/services/email")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class EmailController {

    private final EmailServices emailServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public EmailController(EmailServices emailServices,
                           EventLoopGroup eventLoopGroup,
                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.emailServices = emailServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post
    public Single<HttpResponse<List<Email>>> sendEmail(String subject, String content, boolean html, String... recipients) {
        return Single.fromCallable(() -> emailServices.sendAndSaveEmail(subject, content, html, recipients))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(emails -> (HttpResponse<List<Email>>) HttpResponse.created(emails))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

}
