package com.objectcomputing.checkins.services.email;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller("/services/email")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class EmailController {

    private final EmailServices emailServices;

    public EmailController(EmailServices emailServices) {
        this.emailServices = emailServices;
    }

    @Post
    public Mono<HttpResponse<List<Email>>> sendEmail(String subject, String content, boolean html, String... recipients) {
        return Mono.fromCallable(() -> emailServices.sendAndSaveEmail(subject, content, html, recipients))
                .map(HttpResponse::created);
    }

}
