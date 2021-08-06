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
import java.util.concurrent.ExecutorService;

@Controller("/services/email-notifications")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class MailJetController {

    private final EmailSender emailSender;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public MailJetController(EmailSender emailSender,
                             EventLoopGroup eventLoopGroup,
                             @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.emailSender = emailSender;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post()
    public Single<? extends HttpResponse<?>> sendEmailReceivesStatus(String subject, String content, String... recipients) {
              return Single.fromCallable(() -> emailSender.sendEmailReceivesStatus(subject, content, recipients))
                      .observeOn(Schedulers.from(eventLoopGroup))
                      .map(success -> (HttpResponse<?>) HttpResponse.ok())
                      .subscribeOn(Schedulers.from(ioExecutorService));


    }

}
