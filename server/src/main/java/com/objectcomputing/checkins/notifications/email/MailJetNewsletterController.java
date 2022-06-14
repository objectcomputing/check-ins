package com.objectcomputing.checkins.notifications.email;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<HttpResponse<List<Email>>> sendEmail(String subject, String content, String... recipients) {
        return Mono.fromCallable(() -> emailSender.sendAndSaveEmail(subject, content, recipients))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(emails -> (HttpResponse<List<Email>>) HttpResponse.created(emails))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
