package com.objectcomputing.checkins.notifications.email;

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
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Controller("/services/email")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class MailJetNewsletterController {

    private final EmailSender emailSender;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public MailJetNewsletterController(EmailSender emailSender,
                                       EventLoopGroup eventLoopGroup,
                                       @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.emailSender = emailSender;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post
    public Single<HttpResponse<List<Email>>> sendEmail(String subject, String content, String... recipients) {
        return Single.fromCallable(() -> emailSender.sendAndSaveEmail(subject, content, recipients))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(emails -> (HttpResponse<List<Email>>) HttpResponse.created(emails))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

}
