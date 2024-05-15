package com.objectcomputing.checkins.notifications.email;


import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;

@Controller("/services/email-notifications")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class MailJetNotificationController {

    private final CurrentUserServices currentUserServices;
    private final EmailSender emailSender;

    public MailJetNotificationController(CurrentUserServices currentUserServices,
                                         @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender) {
        this.currentUserServices = currentUserServices;
        this.emailSender = emailSender;
    }

    @Post()
    public Mono<HttpResponse<?>> sendEmailReceivesStatus(String subject, String content, String... recipients) {
        return Mono.fromCallable(currentUserServices::getCurrentUser)
                .map(currentUser -> {
                    String fromName = currentUser.getFirstName() + " " + currentUser.getLastName();
                    return emailSender.sendEmailReceivesStatus(fromName, currentUser.getWorkEmail(), subject, content, recipients);
                })
                .map(success -> {
                    if(success){
                        return HttpResponse.ok();
                    } else {
                        return HttpResponse.serverError();
                    }
                });
    }
}
